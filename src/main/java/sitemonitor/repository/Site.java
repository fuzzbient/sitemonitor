package sitemonitor.repository;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Site implements Serializable {
	private static final long serialVersionUID = 775630256620550240L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String name;
	private String url;
	private String status;
	private long responseTime;
	private String active;
	private String assertText;
	private long failures;
	private long failureLimit;
	private String notify;
	
	@JsonIgnore
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="site")
	private Set<Event> events;
	
	public Site() {
	}
	
	public String getNotifyDisplay() {
		return StringUtils.replace(notify, ",", ", ");
	}
	
	public String getIdentity() {
		return id + "";
	}
	
	public String getFailCountDisplay() {
		return failures + "/" + failureLimit;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public long getFailures() {
		return failures;
	}

	public void setFailures(long failures) {
		this.failures = failures;
	}

	public long getFailureLimit() {
		return failureLimit;
	}

	public void setFailureLimit(long failureLimit) {
		this.failureLimit = failureLimit;
	}

	public String getNotify() {
		return notify;
	}

	public void setNotify(String notify) {
		this.notify = notify;
	}

	public String getAssertText() {
		return assertText;
	}

	public void setAssertText(String assertText) {
		this.assertText = assertText;
	}

	public Set<Event> getEvents() {
		return events;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}

}
