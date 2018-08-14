package com.gy.sched.common.util;


import com.gy.sched.common.constants.Constants;
import com.gy.sched.common.domain.store.ClientGroup;
import com.gy.sched.common.domain.store.Cluster;

/**
 * 分组ID工具
 */
public class GroupIdUtil implements Constants {

	/**
	 * 获取GroupId
	 * @param cluster
	 * @param clientGroup
	 * @param clientGroup
	 * @return
	 */
	public static String getGroupId(Cluster cluster, ClientGroup clientGroup) {
		return cluster.getId() + HORIZONTAL_LINE
				+ clientGroup.getServerGroupId() + HORIZONTAL_LINE
				+ cluster.getJobBackupAmount() + HORIZONTAL_LINE
				+ clientGroup.getId();
	}

	public static String generateGroupId(long clusterId,
			long serverGroupId, int backAmout, long clientGroupId) {

		return clusterId + HORIZONTAL_LINE + serverGroupId
				+ HORIZONTAL_LINE + backAmout + HORIZONTAL_LINE
				+ clientGroupId;
	}

	/**
	 * 获取GroupId
	 * @param cluster
	 * @param job
	 * @param jobBackupAmount
	 * @return
	 */
//	public static String getGroupId(Cluster cluster, Job job, int jobBackupAmount) {
//		return cluster.getId() + HORIZONTAL_LINE
//				+ job.getServerGroupId() + HORIZONTAL_LINE + jobBackupAmount
//				+ HORIZONTAL_LINE + job.getClientGroupId();
//	}

	/**
	 * 检查分组ID
	 * 
	 * @param groupId
	 * @return
	 */
	public static boolean checkGroupId(String groupId) {
		if (StringUtil.isBlank(groupId)) {
			throw new RuntimeException(
					"groupId is null! please set groupId, you can get groupId from console page");
		}
		String[] s = groupId.split(HORIZONTAL_LINE);
		if (s.length != 4) {
			throw new RuntimeException(
					"groupId is error! please check groupId, like this format xxx-xxx-xxx-xxx. but you set groupId:"
							+ groupId);
		}
		return true;
	}

    /**
     * 检查分组ID
     *
     * @param groupId
     * @return
     */
    public static boolean checkClientGroupId(String groupId) {
        if (StringUtil.isBlank(groupId)) {
            return false;
        }
        String[] s = groupId.split(HORIZONTAL_LINE);
        if (s.length != 4) {
            return false;
        }
        return true;
    }

	/**
	 * 解析groupId获取Cluster
	 * 
	 * @param groupId
	 * @return
	 */
	public static Cluster getCluster(String groupId) {
		String[] s = groupId.split(HORIZONTAL_LINE);
		Cluster cluster = new Cluster();
		cluster.setId(Long.parseLong(s[0]));
		cluster.setJobBackupAmount(Integer.parseInt(s[2]));
		return cluster;
	}
	
	/**
	 * 解析groupId获取ClientGroup
	 * @param groupId
	 * @return
	 */
	public static ClientGroup getClientGroup(String groupId) {
		String[] s = groupId.split(HORIZONTAL_LINE);
		ClientGroup clientGroup = new ClientGroup();
		clientGroup.setServerGroupId(Long.parseLong(s[1]));
		clientGroup.setId(Long.parseLong(s[3]));
		return clientGroup;
	}

}
