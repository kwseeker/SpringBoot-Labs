package top.kwseeker.springboot.lab03.springsecurity.user.dal.mysql;

import org.apache.ibatis.annotations.Mapper;
import top.kwseeker.springboot.lab03.springsecurity.common.mybatis.LambdaQueryWrapperX;
import top.kwseeker.springboot.lab03.springsecurity.common.page.PageResult;
import top.kwseeker.springboot.lab03.springsecurity.user.controller.vo.UserPageReqVO;
import top.kwseeker.springboot.lab03.springsecurity.user.dal.dataobject.AdminUserDO;

import java.util.Collection;
import java.util.List;

@Mapper
public interface AdminUserMapper extends BaseMapperX<AdminUserDO> {

    default AdminUserDO selectByUsername(String username) {
        return selectOne(AdminUserDO::getUsername, username);
    }

    default AdminUserDO selectByEmail(String email) {
        return selectOne(AdminUserDO::getEmail, email);
    }

    default AdminUserDO selectByMobile(String mobile) {
        return selectOne(AdminUserDO::getMobile, mobile);
    }

    default PageResult<AdminUserDO> selectPage(UserPageReqVO reqVO, Collection<Long> deptIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserDO>()
                .likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
                .eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime())
                .inIfPresent(AdminUserDO::getDeptId, deptIds)
                .orderByDesc(AdminUserDO::getId));
    }

    //default List<AdminUserDO> selectList(UserExportReqVO reqVO, Collection<Long> deptIds) {
    //    return selectList(new LambdaQueryWrapperX<AdminUserDO>()
    //            .likeIfPresent(AdminUserDO::getUsername, reqVO.getUsername())
    //            .likeIfPresent(AdminUserDO::getMobile, reqVO.getMobile())
    //            .eqIfPresent(AdminUserDO::getStatus, reqVO.getStatus())
    //            .betweenIfPresent(AdminUserDO::getCreateTime, reqVO.getCreateTime())
    //            .inIfPresent(AdminUserDO::getDeptId, deptIds));
    //}
    //
    //default List<AdminUserDO> selectListByNickname(String nickname) {
    //    return selectList(new LambdaQueryWrapperX<AdminUserDO>().like(AdminUserDO::getNickname, nickname));
    //}
    //
    //default List<AdminUserDO> selectListByStatus(Integer status) {
    //    return selectList(AdminUserDO::getStatus, status);
    //}
    //
    //default List<AdminUserDO> selectListByDeptIds(Collection<Long> deptIds) {
    //    return selectList(AdminUserDO::getDeptId, deptIds);
    //}

}
