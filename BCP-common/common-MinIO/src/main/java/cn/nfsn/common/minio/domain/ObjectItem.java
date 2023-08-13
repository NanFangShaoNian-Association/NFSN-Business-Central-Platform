package cn.nfsn.common.minio.domain;

public class ObjectItem {
    private String objectName;
    private Long size;

    public ObjectItem() {
    }

    public ObjectItem(String objectName, Long size) {
        this.objectName = objectName;
        this.size = size;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ObjectItem{" +
                "objectName='" + objectName + '\'' +
                ", size=" + size +
                '}';
    }
}
