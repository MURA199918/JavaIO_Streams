public class Todo {

    private int id;
    private String summary;
    private String desc;

    public int getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public String getDesc() {
        return desc;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setSummary(String summary){
        this.summary = summary;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public Todo(int id, String summary, String desc) {
        this.id = id;
        this.summary = summary;
        this.desc = desc;
    }
}
