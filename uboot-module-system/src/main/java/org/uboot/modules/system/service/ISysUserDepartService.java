package org.uboot.modules.system.service;


import java.util.List;

import org.uboot.modules.system.entity.SysUser;
import org.uboot.modules.system.entity.SysUserDepart;
import org.uboot.modules.system.model.DepartIdModel;


import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * SysUserDpeart用户组织机构service
 * </p>
 * @Author ZhiLin
 *
 */
public interface ISysUserDepartService extends IService<SysUserDepart> {


	/**
	 * 根据指定用户id查询部门信息
	 * @param userId
	 * @return
	 */
	List<DepartIdModel> queryDepartIdsOfUser(String userId);


	/**
	 * 根据部门id查询用户信息
	 * @param depId
	 * @return
	 */
	List<SysUser> queryUserByDepId(String depId);


    /**
     * 根据部门code和用户id删除关联关系,并新建用户与部门关系
     * @param userId
     * @return
     */
	int updateByDepCodeAndUser(String oldDeptCode, String userId, String newDeptCode);
}
