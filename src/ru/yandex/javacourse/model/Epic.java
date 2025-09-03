package ru.yandex.javacourse.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIdList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtasksIdList() {
        return subtasksIdList;
    }

    public void setSubtasksIdList(ArrayList<Integer> subtasksIdList) {
        this.subtasksIdList = subtasksIdList;
    }

    public void addSubtaskId(Integer subtaskId) {
        subtasksIdList.add(subtaskId);
    }

    public void deleteSubtaskId(Integer subtaskId) {
        subtasksIdList.remove(subtaskId);
    }

    public void deleteSubtasksIdList() {
        subtasksIdList.clear();
    }


    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() + '\'' +
                ", subtasksIdList=" + subtasksIdList +
                '}';
    }
}
