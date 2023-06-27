package com.nova.cls.data.models;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.Objects;

public class Group {
    @JsonView(Views.ReadView.class)
    private Long groupId;
    @JsonView({Views.ReadView.class, Views.CreateView.class, Views.UpdateView.class})
    private String groupName;
    @JsonView({Views.ReadView.class, Views.CreateView.class, Views.UpdateView.class})
    private String description;

    public Group() {
    }

    public Group(Long groupId, String groupName, String description) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.description = description;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Group group = (Group) o;
        return Objects.equals(getGroupId(), group.getGroupId()) && Objects.equals(getGroupName(), group.getGroupName())
            && Objects.equals(getDescription(), group.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGroupName(), getDescription());
    }

    @Override
    public String toString() {
        return "Group {" + "groupId=" + getGroupId() + ", groupName='" + getGroupName() + '\'' + ", description='"
            + getDescription() + '\'' + '}';
    }
}
