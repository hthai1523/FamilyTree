package Model;

public class Person {
    private int id;
    private String name;
    private int age;
    private String address;
    private String gender;
    private String isAlive;

    private String note;

    private byte[] picture;

    public Person(int id, String name, int age, String address, String gender, String isAlive, String note, byte[] picture) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
        this.gender = gender;
        this.isAlive = isAlive;
        this.note = note;
        this.picture = picture;
    }


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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String isAlive() {
        return isAlive;
    }

    public void setAlive(String alive) {
        isAlive = alive;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public byte[] getImage(){
        return picture;
    }

}
