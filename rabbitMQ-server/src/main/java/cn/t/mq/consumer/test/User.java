package cn.t.mq.consumer.test;

import java.io.Serializable;

public class User  implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -825535278329478800L;

	private int id;

    private String name;
    
    private String addr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	/**
	 * @return the addr
	 */
	public String getAddr() {
		return addr;
	}

	/**
	 * @param addr the addr to set
	 */
	public void setAddr(String addr) {
		this.addr = addr;
	}
}
