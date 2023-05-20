public class Booking {
    private int id;
    private int userId;
    private int busId;

    public Booking(int id, int userId, int busId) {
        this.id = id;
        this.userId = userId;
        this.busId = busId;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getBusId() {
        return busId;
    }
}