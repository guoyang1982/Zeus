package com.gy.sched.common.domain.store;

import java.util.Date;

/**
 * server 和server发出的job instance列表的映射关系
 */
public class ServerJobInstanceMapping {
    private long id;
    private String server;
    private String jobInstanceInfo;
    private Date gmtCreate;
    private Date gmtModified;
    
    private long jobInstanceId;
    private long jobId;
    private int jobType;
    private String appName;
    private boolean compensation = false;
    /** 客户端ID */
	private String clientId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getJobInstanceInfo() {
        return jobInstanceInfo;
    }

    public void setJobInstanceInfo(String jobInstanceInfo) {
        this.jobInstanceInfo = jobInstanceInfo;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public long getJobInstanceId() {
		return jobInstanceId;
	}

	public void setJobInstanceId(long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public int getJobType() {
		return jobType;
	}

	public void setJobType(int jobType) {
		this.jobType = jobType;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public boolean isCompensation() {
		return compensation;
	}

	public void setCompensation(boolean compensation) {
		this.compensation = compensation;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

    /**
     * Only jobInstanceId is the identity,
     * other fields are additional information.
     */
    public static class JobInstanceKey {
        private long jobInstanceId;
        private long jobId;
        private int jobType;
        private String appName;
        private boolean compensation = false;
        
        public JobInstanceKey() {
        	
        }
        
        public JobInstanceKey(long jobInstanceId, long jobId, int jobType, String appName, boolean compensation) {
        	this.jobInstanceId = jobInstanceId;
        	this.jobId = jobId;
        	this.jobType = jobType;
        	this.appName = appName;
        	this.compensation = compensation;
        }

        public long getJobInstanceId() {
            return jobInstanceId;
        }

        public void setJobInstanceId(long jobInstanceId) {
            this.jobInstanceId = jobInstanceId;
        }

        public long getJobId() {
            return jobId;
        }

        public void setJobId(long jobId) {
            this.jobId = jobId;
        }

        public int getJobType() {
            return jobType;
        }

        public void setJobType(int jobType) {
            this.jobType = jobType;
        }

        public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public boolean isCompensation() {
			return compensation;
		}

		public void setCompensation(boolean compensation) {
			this.compensation = compensation;
		}

		@Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof JobInstanceKey)) {
                return false;
            }

            JobInstanceKey that = (JobInstanceKey) o;

            if (jobInstanceId != that.jobInstanceId) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return (int) (jobInstanceId ^ (jobInstanceId >>> 32));
        }

		@Override
		public String toString() {
			return "JobInstanceKey [jobInstanceId=" + jobInstanceId
					+ ", jobId=" + jobId + ", jobType=" + jobType
					+ ", appName=" + appName + ", compensation=" + compensation
					+ "]";
		}

    }

	@Override
	public String toString() {
		return "ServerJobInstanceMapping [id=" + id + ", server=" + server
				+ ", jobInstanceInfo=" + jobInstanceInfo + ", gmtCreate="
				+ gmtCreate + ", gmtModified=" + gmtModified
				+ ", jobInstanceId=" + jobInstanceId + ", jobId=" + jobId
				+ ", jobType=" + jobType + ", appName=" + appName
				+ ", compensation=" + compensation + ", clientId=" + clientId
				+ "]";
	}

}
