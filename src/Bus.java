public class Bus {
    private int id;
    private String departureStation;
    private String arrivalStation;
    private String time;
    private int seatsAvailable;

    public Bus(int id, String departureStation, String arrivalStation, String time, int seatsAvailable) {
        this.id = id;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.time = time;
        this.seatsAvailable = seatsAvailable;
    }

    public int getId() {
        return id;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public String getTime() {
        return time;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }
}