package org.uboot.modules.system.service.impl;

import java.util.List;
import java.util.Map;

import org.uboot.common.exception.UBootException;
import org.uboot.common.util.YouBianCodeUtil;
import org.uboot.common.util.oConvertUtils;
import org.uboot.modules.system.entity.SysCategory;
import org.uboot.modules.system.mapper.SysCategoryMapper;
import org.uboot.modules.system.model.TreeSelectModel;
import org.uboot.modules.system.service.ISysCategoryService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 分类字典
 * @Author: jeecg-boot
 * @Date:   2019-05-29
 * @Version: V1.0
 */
@Service
public class SysCategoryServiceImpl extends ServiceImpl<SysCategoryMapper, SysCategory> implements ISysCategoryService {

	@Override
	public void addSysCategory(SysCategory sysCategory) {
		String categoryCode = "";
		String categoryPid = ISysCategoryService.ROOT_PID_VALUE;
		String parentCode = null;
		if(oConvertUtils.isNotEmpty(sysCategory.getPid())){
			categoryPid = sysCategory.getPid();

			//PID 不是根节点 说明需要设置父节点 hasChild 为1
			if(!ISysCategoryService.ROOT_PID_VALUE.equals(categoryPid)){
				SysCategory parent = baseMapper.selectById(categoryPid);
				parentCode = parent.getCode();
				if(parent!=null && !"1".equals(parent.getHasChild())){
					parent.setHasChild("1");
					baseMapper.updateById(parent);
				}
			}
		}
		/*
		* 分成三种情况
		* 1.数据库无数据 调用YouBianCodeUtil.getNextYouBianCode(null);
		* 2.添加子节点，无兄弟元素 YouBianCodeUtil.getSubYouBianCode(parentCode,null);
		* 3.添加子节点有兄弟元素 YouBianCodeUtil.getNextYouBianCode(lastCode);
		* */
		//找同类 确定上一个最大的code值
		LambdaQueryWrapper<SysCategory> query = new LambdaQueryWrapper<SysCategory>()
				.eq(SysCategory::getPid,categoryPid)
				.orderByDesc(SysCategory::getCode);
		List<SysCategory> list = baseMapper.selectList(query);
		if(list==null || list.size()==0){
			if(ISysCategoryService.ROOT_PID_VALUE.equals(categoryPid)){
				//情况1
				categoryCode = YouBianCodeUtil.getNextYouBianCode(null);
			}else{
				//情况2
				categoryCode = YouBianCodeUtil.getSubYouBianCode(parentCode,null);
			}
		}else{
			//情况3
			categoryCode = YouBianCodeUtil.getNextYouBianCode(list.get(0).getCode());
		}
		sysCategory.setCode(categoryCode);
		sysCategory.setPid(categoryPid);
		baseMapper.insert(sysCategory);
	}

	@Override
	public void updateSysCategory(SysCategory sysCategory) {
		if(oConvertUtils.isEmpty(sysCategory.getPid())){
			sysCategory.setPid(ISysCategoryService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChild 为1
			SysCategory parent = baseMapper.selectById(sysCategory.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.updateById(sysCategory);
	}

	@Override
	public List<TreeSelectModel> queryListByCode(String pcode) throws UBootException{
		String pid = ROOT_PID_VALUE;
		if(oConvertUtils.isNotEmpty(pcode)) {
			List<SysCategory> list = baseMapper.selectList(new LambdaQueryWrapper<SysCategory>().eq(SysCategory::getCode, pcode));
			if(list==null || list.size() ==0) {
				throw new UBootException("该编码【"+pcode+"】不存在，请核实!");
			}
			if(list.size()>1) {
				throw new UBootException("该编码【"+pcode+"】存在多个，请核实!");
			}
			pid = list.get(0).getId();
		}
		return baseMapper.queryListByPid(pid,null);
	}

	@Override
	public List<TreeSelectModel> queryListByPid(String pid) {
		if(oConvertUtils.isEmpty(pid)) {
			pid = ROOT_PID_VALUE;
		}
		return baseMapper.queryListByPid(pid,null);
	}

	@Override
	public List<TreeSelectModel> queryListByPid(String pid, Map<String, String> condition) {
		if(oConvertUtils.isEmpty(pid)) {
			pid = ROOT_PID_VALUE;
		}
		return baseMapper.queryListByPid(pid,condition);
	}

	@Override
	public String queryIdByCode(String code) {
		return baseMapper.queryIdByCode(code);
	}

}
