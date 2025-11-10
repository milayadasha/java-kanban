package ru.yandex.javacourse.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIdList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Integer> getSubtasksIdList() {
        return subtasksIdList;
    }

    public void setSubtasksIdList(ArrayList<Integer> subtasksIdList) {
        this.subtasksIdList = subtasksIdList;
    }

    public void addSubtaskId(Integer subtaskId) {
        if (subtaskId.equals(this.getId())) {
            return;
        }
        subtasksIdList.add(subtaskId);
    }

    public void deleteSubtaskId(Integer subtaskId) {
        subtasksIdList.remove(subtaskId);
    }

    public void deleteSubtasksIdList() {
        subtasksIdList.clear();
    }

    @Override
    public Epic getCopy() {
        Epic epicCopy = new Epic(this.getName(), this.getDescription());
        epicCopy.setId(this.getId());
        epicCopy.setStatus(this.getStatus());
        epicCopy.setSubtasksIdList(new ArrayList<>(this.getSubtasksIdList()));
        epicCopy.setDuration(this.getDuration());
        epicCopy.setStartTime(this.getStartTime());
        return epicCopy;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() + '\'' +
                ", startTime=" + getStartTime() +
                ", duration =" + getDuration() +
                ", subtasksIdList=" + subtasksIdList +
                '}';
    }
}
