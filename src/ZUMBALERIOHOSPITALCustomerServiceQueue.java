import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class ZUMBALERIOHOSPITALCustomerServiceQueue {

    static Scanner input = new Scanner(System.in);

    
    static ArrayList<Integer> customerIds = new ArrayList<>();
    static ArrayList<String> customerNames = new ArrayList<>();
    static ArrayList<String> concerns = new ArrayList<>();
    static ArrayList<Integer> durations = new ArrayList<>();  
    static ArrayList<String> statuses = new ArrayList<>();     
  
    static Queue<Integer> serviceQueue = new LinkedList<>();

    static int nextId = 1001;

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            showMenu();
            int choice = readInt("Enter your choice: ", 1, 13);
            System.out.println();
            switch (choice) {
                case 1:  registerCustomer(); break;
                case 2:  displayRecords("ALL CUSTOMER RECORDS"); break;
                case 3:  enqueueCustomer(); break;
                case 4:  dequeueCustomer(); break;
                case 5:  peekQueue(); break;
                case 6:  displayQueue(); break;
                case 7:  checkQueueEmpty(); break;
                case 8:  sortByNameBubble(); break;
                case 9:  sortByDurationInsertion(); break;
                case 10: previewQueueByDuration(); break;
                case 11: searchCustomer(); break;
                case 12: deleteCustomer(); break;
                case 13:
                    System.out.println("Thank you for using the Customer Service Queue System. Goodbye!");
                    running = false;
                    break;
            }
            if (running) {
                System.out.print("\nPress Enter to continue...");
                input.nextLine();
            }
        }
        input.close();
    }

    // =====================================================================
    //  MENU
    // =====================================================================
    static void showMenu() {
        System.out.println("\n=======================================================");
        System.out.println("ZUMBALERIO HOSPITAL: CUSTOMER SERVICE QUEUE MANAGEMENT SYSTEM");
        System.out.println("=======================================================");
        System.out.println(" RECORDS");
        System.out.println("  1. Register new customer");
        System.out.println("  2. Display all customer records");
        System.out.println(" QUEUE");
        System.out.println("  3. Add customer to service queue (Enqueue)");
        System.out.println("  4. Serve next customer (Dequeue)");
        System.out.println("  5. Peek at next customer");
        System.out.println("  6. Display waiting queue");
        System.out.println("  7. Check if queue is empty");
        System.out.println(" SORT / SEARCH");
        System.out.println("  8. Sort records by name A-Z (Bubble Sort)");
        System.out.println("  9. Sort records by service duration (Insertion Sort)");
        System.out.println(" 10. Preview waiting queue by shortest duration (Selection Sort)");
        System.out.println(" 11. Search for a customer");
        System.out.println(" 12. Delete a customer record");
        System.out.println(" 13. Exit");
        System.out.println("=====================================================");
    }

   
    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Invalid input. Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    // =====================================================================
    //  RECORD OPERATIONS
    // =====================================================================
    static int findIndexById(int id) {
        for (int i = 0; i < customerIds.size(); i++) {
            if (customerIds.get(i) == id) {
                return i;
            }
        }
        return -1;
    }

    static void registerCustomer() {
        System.out.println("--- Register New Customer ---");
        String name = readNonEmpty("Customer name: ");
        String concern = readNonEmpty("Concern / service type: ");
        int duration = readInt("Estimated service duration in minutes (1-240): ", 1, 240);

        customerIds.add(nextId);
        customerNames.add(name);
        concerns.add(concern);
        durations.add(duration);
        statuses.add("Registered");

        System.out.println("Customer registered successfully. Assigned ID: " + nextId);
        nextId++;

        String again = readNonEmpty("Add this customer to the service queue now? (y/n): ");
        if (again.equalsIgnoreCase("y")) {
            int id = customerIds.get(customerIds.size() - 1);
            serviceQueue.add(id);
            statuses.set(customerIds.size() - 1, "Waiting");
            System.out.println(name + " has been added to the queue. Position: " + serviceQueue.size());
        }
    }

    static void printTableHeader() {
        System.out.printf("%-6s %-20s %-25s %-10s %-10s%n", "ID", "Name", "Concern", "Duration", "Status");
        System.out.println("-----------------------------------------------------------------------");
    }

    static void printRecordRow(int i) {
        System.out.printf("%-6d %-20s %-25s %-10s %-10s%n",
                customerIds.get(i), customerNames.get(i), concerns.get(i),
                durations.get(i) + " min", statuses.get(i));
    }

    static void displayRecords(String title) {
        System.out.println("--- " + title + " ---");
        if (customerIds.isEmpty()) {
            System.out.println("No customer records found. Register a customer first.");
            return;
        }
        printTableHeader();
        for (int i = 0; i < customerIds.size(); i++) {
            printRecordRow(i);
        }
        System.out.println("Total records: " + customerIds.size());
    }

    static void deleteCustomer() {
        System.out.println("--- Delete Customer Record ---");
        if (customerIds.isEmpty()) {
            System.out.println("No customer records to delete.");
            return;
        }
        int id = readInt("Enter customer ID to delete: ", 1, Integer.MAX_VALUE);
        int index = findIndexById(id);
        if (index == -1) {
            System.out.println("No customer found with ID " + id + ".");
            return;
        }
        String name = customerNames.get(index);

        // keep all parallel lists consistent
        customerIds.remove(index);
        customerNames.remove(index);
        concerns.remove(index);
        durations.remove(index);
        statuses.remove(index);

        // also remove from queue if the customer was waiting
        if (serviceQueue.remove(Integer.valueOf(id))) {
            System.out.println("Customer was also removed from the waiting queue.");
        }
        System.out.println("Record for " + name + " (ID " + id + ") deleted.");
    }

    // =====================================================================
    //  QUEUE OPERATIONS
    // =====================================================================
    static void enqueueCustomer() {
        System.out.println("--- Add Customer to Queue (Enqueue) ---");
        if (customerIds.isEmpty()) {
            System.out.println("No customer records found. Register a customer first.");
            return;
        }
        displayRecords("REGISTERED CUSTOMERS");
        int id = readInt("\nEnter customer ID to add to the queue: ", 1, Integer.MAX_VALUE);
        int index = findIndexById(id);
        if (index == -1) {
            System.out.println("Operation failed: no customer found with ID " + id + ".");
            return;
        }
        if (statuses.get(index).equals("Waiting")) {
            System.out.println("Operation failed: " + customerNames.get(index) + " is already in the queue.");
            return;
        }
        if (statuses.get(index).equals("Served")) {
            System.out.println("Operation failed: " + customerNames.get(index) + " has already been served.");
            return;
        }
        serviceQueue.add(id);
        statuses.set(index, "Waiting");
        System.out.println(customerNames.get(index) + " added to the queue. Position: " + serviceQueue.size());
    }

    static void dequeueCustomer() {
        System.out.println("--- Serve Next Customer (Dequeue) ---");
        if (serviceQueue.isEmpty()) {
            System.out.println("The queue is empty. No customer to serve.");
            return;
        }
        int id = serviceQueue.poll();
        int index = findIndexById(id);
        statuses.set(index, "Served");
        System.out.println("Now serving: " + customerNames.get(index) + " (ID " + id + ")");
        System.out.println("Concern    : " + concerns.get(index));
        System.out.println("Est. time  : " + durations.get(index) + " minutes");
        System.out.println("Customers still waiting: " + serviceQueue.size());
    }

    static void peekQueue() {
        System.out.println("--- Peek at Next Customer ---");
        if (serviceQueue.isEmpty()) {
            System.out.println("The queue is empty. Nobody is waiting.");
            return;
        }
        int id = serviceQueue.peek();
        int index = findIndexById(id);
        System.out.println("Next in line: " + customerNames.get(index) + " (ID " + id + ")");
        System.out.println("Concern     : " + concerns.get(index));
        System.out.println("(Customer remains in the queue.)");
    }

    static void displayQueue() {
        System.out.println("--- Current Waiting Queue (front to back) ---");
        if (serviceQueue.isEmpty()) {
            System.out.println("The queue is empty.");
            return;
        }
        System.out.printf("%-4s %-6s %-20s %-25s %-10s%n", "No.", "ID", "Name", "Concern", "Duration");
        System.out.println("---------------------------------------------------------------------");
        int position = 1;
        int totalWait = 0;
        for (int id : serviceQueue) {
            int index = findIndexById(id);
            System.out.printf("%-4d %-6d %-20s %-25s %-10s%n", position, id,
                    customerNames.get(index), concerns.get(index), durations.get(index) + " min");
            totalWait += durations.get(index);
            position++;
        }
        System.out.println("Customers waiting: " + serviceQueue.size()
                + " | Total estimated service time: " + totalWait + " min");
    }

    static void checkQueueEmpty() {
        System.out.println("--- Queue Status ---");
        if (serviceQueue.isEmpty()) {
            System.out.println("The queue is EMPTY.");
        } else {
            System.out.println("The queue is NOT empty. " + serviceQueue.size() + " customer(s) waiting.");
        }
    }

    // =====================================================================
    //  SORTING ALGORITHMS (manually implemented)
    // =====================================================================

    // Swaps record i and j in EVERY parallel list so indexes stay consistent.
    static void swapRecords(int i, int j) {
        int tempId = customerIds.get(i);
        customerIds.set(i, customerIds.get(j));
        customerIds.set(j, tempId);

        String tempName = customerNames.get(i);
        customerNames.set(i, customerNames.get(j));
        customerNames.set(j, tempName);

        String tempConcern = concerns.get(i);
        concerns.set(i, concerns.get(j));
        concerns.set(j, tempConcern);

        int tempDuration = durations.get(i);
        durations.set(i, durations.get(j));
        durations.set(j, tempDuration);

        String tempStatus = statuses.get(i);
        statuses.set(i, statuses.get(j));
        statuses.set(j, tempStatus);
    }

    // Bubble Sort: alphabetical by customer name (case-insensitive).
    // Suitable here because customer lists are small and it is simple to trace;
    // repeated adjacent comparisons make the A-Z ordering easy to explain.
    static void sortByNameBubble() {
        System.out.println("--- Sort by Name A-Z (Bubble Sort) ---");
        if (customerIds.size() < 2) {
            System.out.println("Not enough records to sort. At least 2 are needed.");
            return;
        }
        displayRecords("BEFORE SORTING");

        int n = customerIds.size();
        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - pass; j++) {
                if (customerNames.get(j).compareToIgnoreCase(customerNames.get(j + 1)) > 0) {
                    swapRecords(j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break; // already sorted
            }
        }
        System.out.println();
        displayRecords("AFTER SORTING (Name A-Z)");
        System.out.println("Note: sorting records does not change the FIFO order of the queue.");
    }

    // Insertion Sort: ascending by service duration.
    // Suitable because it works well on small or partly sorted lists, such as
    // records that are re-sorted after only a few new customers were added.
    static void sortByDurationInsertion() {
        System.out.println("--- Sort by Service Duration (Insertion Sort) ---");
        if (customerIds.size() < 2) {
            System.out.println("Not enough records to sort. At least 2 are needed.");
            return;
        }
        displayRecords("BEFORE SORTING");

        for (int i = 1; i < customerIds.size(); i++) {
            int j = i;
            // shift the record left while it is shorter than the one before it
            while (j > 0 && durations.get(j - 1) > durations.get(j)) {
                swapRecords(j - 1, j);
                j--;
            }
        }
        System.out.println();
        displayRecords("AFTER SORTING (Shortest duration first)");
        System.out.println("Note: sorting records does not change the FIFO order of the queue.");
    }

    // Selection Sort on a TEMPORARY copy of the queue (IDs only).
    // The real queue is never modified, so FIFO order is preserved.
    // Suitable because it makes few swaps and the preview list is small.
    static void previewQueueByDuration() {
        System.out.println("--- Waiting Queue Preview: Shortest Duration First (Selection Sort) ---");
        if (serviceQueue.isEmpty()) {
            System.out.println("The queue is empty. Nothing to preview.");
            return;
        }
        ArrayList<Integer> temp = new ArrayList<>(serviceQueue); // copy of IDs in FIFO order

        System.out.println("Actual queue order (FIFO):");
        for (int k = 0; k < temp.size(); k++) {
            int idx = findIndexById(temp.get(k));
            System.out.println("  " + (k + 1) + ". " + customerNames.get(idx) + " - " + durations.get(idx) + " min");
        }

        for (int i = 0; i < temp.size() - 1; i++) {
            int minPos = i;
            for (int j = i + 1; j < temp.size(); j++) {
                int durJ = durations.get(findIndexById(temp.get(j)));
                int durMin = durations.get(findIndexById(temp.get(minPos)));
                if (durJ < durMin) {
                    minPos = j;
                }
            }
            if (minPos != i) {
                int holder = temp.get(i);
                temp.set(i, temp.get(minPos));
                temp.set(minPos, holder);
            }
        }

        System.out.println("\nPreview sorted by shortest duration:");
        for (int k = 0; k < temp.size(); k++) {
            int idx = findIndexById(temp.get(k));
            System.out.println("  " + (k + 1) + ". " + customerNames.get(idx) + " - " + durations.get(idx) + " min");
        }
        System.out.println("\nThis is a preview only. The real queue still serves in FIFO order.");
    }

    // =====================================================================
    //  SEARCH
    // =====================================================================
    static void searchCustomer() {
        System.out.println("--- Search Customer ---");
        if (customerIds.isEmpty()) {
            System.out.println("No customer records to search.");
            return;
        }
        System.out.println("1. Search by ID");
        System.out.println("2. Search by name (partial match allowed)");
        int option = readInt("Choose search type: ", 1, 2);

        boolean found = false;
        if (option == 1) {
            int id = readInt("Enter customer ID: ", 1, Integer.MAX_VALUE);
            for (int i = 0; i < customerIds.size(); i++) {
                if (customerIds.get(i) == id) {
                    printTableHeader();
                    printRecordRow(i);
                    found = true;
                    break;
                }
            }
        } else {
            String keyword = readNonEmpty("Enter name to search: ").toLowerCase();
            for (int i = 0; i < customerNames.size(); i++) {
                if (customerNames.get(i).toLowerCase().contains(keyword)) {
                    if (!found) {
                        printTableHeader();
                    }
                    printRecordRow(i);
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("No matching customer record found.");
        }
    }
}


