import java.util.ArrayList;
import java.util.List;

public class Road {
    public List<Vehicle<? extends Passenger>> carsInRoad = new ArrayList<>();

    public int getCountOfHumans() {
        int count = 0;
        for (Vehicle<? extends Passenger> vehicle : carsInRoad) {
            count += vehicle.getOccupiedSeats();
        }
        return count;
    }

    public void addCarToRoad(Vehicle<? extends Passenger> vehicle) {
        carsInRoad.add(vehicle);
    }
}
