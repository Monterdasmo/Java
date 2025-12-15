
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehicleTest {

    @Test
    public void testBusCanCarryAllPassengers() throws VehicleFullException {
        Bus bus = new Bus(3);

        bus.boardPassenger(new RegularPassenger("John"));
        bus.boardPassenger(new Firefighter("Mike"));
        bus.boardPassenger(new Policeman("Steve"));

        assertEquals(3, bus.getOccupiedSeats());
        assertEquals(3, bus.getMaxCapacity());
    }

    @Test
    public void testTaxiCanCarryAllPassengers() throws VehicleFullException {
        Taxi taxi = new Taxi(4);

        taxi.boardPassenger(new RegularPassenger("Alice"));
        taxi.boardPassenger(new Firefighter("Bob"));
        taxi.boardPassenger(new Policeman("Charlie"));

        assertEquals(3, taxi.getOccupiedSeats());
    }

    @Test
    public void testFireTruckCanCarryOnlyFirefighters() throws VehicleFullException {
        FireTruck fireTruck = new FireTruck(5);

        fireTruck.boardPassenger(new Firefighter("Alex"));
        fireTruck.boardPassenger(new Firefighter("Max"));

        assertEquals(2, fireTruck.getOccupiedSeats());
    }

    @Test
    public void testPoliceCarCanCarryOnlyPolicemen() throws VehicleFullException {
        PoliceCar policeCar = new PoliceCar(4);

        policeCar.boardPassenger(new Policeman("Officer1"));
        policeCar.boardPassenger(new Policeman("Officer2"));

        assertEquals(2, policeCar.getOccupiedSeats());
    }

    @Test
    public void testVehicleFullException() throws VehicleFullException {
        Taxi taxi = new Taxi(2);

        taxi.boardPassenger(new RegularPassenger("Passenger1"));
        taxi.boardPassenger(new RegularPassenger("Passenger2"));

        assertThrows(VehicleFullException.class, () -> {
            taxi.boardPassenger(new RegularPassenger("Passenger3"));
        });
    }

    @Test
    public void testDisembarkPassenger() throws VehicleFullException, PassengerNotFoundException {
        Bus bus = new Bus(5);
        RegularPassenger passenger = new RegularPassenger("John");

        bus.boardPassenger(passenger);
        assertEquals(1, bus.getOccupiedSeats());

        bus.disembarkPassenger(passenger);
        assertEquals(0, bus.getOccupiedSeats());
    }

    @Test
    public void testPassengerNotFoundException() throws VehicleFullException {
        Bus bus = new Bus(5);
        RegularPassenger passenger = new RegularPassenger("John");

        assertThrows(PassengerNotFoundException.class, () -> {
            bus.disembarkPassenger(passenger);
        });
    }

    @Test
    public void testRoadCountOfHumans() throws VehicleFullException {
        Road road = new Road();

        Bus bus = new Bus(10);
        bus.boardPassenger(new RegularPassenger("P1"));
        bus.boardPassenger(new RegularPassenger("P2"));
        bus.boardPassenger(new Firefighter("F1"));

        Taxi taxi = new Taxi(4);
        taxi.boardPassenger(new RegularPassenger("P3"));
        taxi.boardPassenger(new Policeman("PO1"));

        FireTruck fireTruck = new FireTruck(6);
        fireTruck.boardPassenger(new Firefighter("F2"));
        fireTruck.boardPassenger(new Firefighter("F3"));

        PoliceCar policeCar = new PoliceCar(4);
        policeCar.boardPassenger(new Policeman("PO2"));

        road.addCarToRoad(bus);
        road.addCarToRoad(taxi);
        road.addCarToRoad(fireTruck);
        road.addCarToRoad(policeCar);

        assertEquals(8, road.getCountOfHumans());
    }

    @Test
    public void testMultipleDisembark() throws VehicleFullException, PassengerNotFoundException {
        Bus bus = new Bus(5);
        RegularPassenger p1 = new RegularPassenger("P1");
        RegularPassenger p2 = new RegularPassenger("P2");
        Firefighter f1 = new Firefighter("F1");

        bus.boardPassenger(p1);
        bus.boardPassenger(p2);
        bus.boardPassenger(f1);

        assertEquals(3, bus.getOccupiedSeats());

        bus.disembarkPassenger(p1);
        assertEquals(2, bus.getOccupiedSeats());

        bus.disembarkPassenger(f1);
        assertEquals(1, bus.getOccupiedSeats());

        bus.disembarkPassenger(p2);
        assertEquals(0, bus.getOccupiedSeats());
    }
}