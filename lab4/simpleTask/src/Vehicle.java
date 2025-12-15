import java.util.ArrayList;
import java.util.List;

public abstract class Vehicle<T extends Passenger> {
    private int maxCapacity;
    private List<T> passengers;

    public Vehicle(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.passengers = new ArrayList<>();
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getOccupiedSeats() {
        return passengers.size();
    }

    public void boardPassenger(T passenger) throws VehicleFullException {
        if (passengers.size() >= maxCapacity) {
            throw new VehicleFullException("Vehicle is full!");
        }
        passengers.add(passenger);
    }

    public void disembarkPassenger(T passenger) throws PassengerNotFoundException {
        if (!passengers.remove(passenger)) {
            throw new PassengerNotFoundException("Passenger not found in vehicle!");
        }
    }

    public List<T> getPassengers() {
        return new ArrayList<>(passengers);
    }
}
