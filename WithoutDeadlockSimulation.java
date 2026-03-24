import java.util.concurrent.Semaphore;

class Account {
    String name;
    int balance;
    Semaphore lock = new Semaphore(1);

    Account(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }
}

class Transfer {

    static void transfer(Account from, Account to, int amount) {
        Account first;
        Account second;

        // 🔑 Enforce consistent lock order (by name)
        if (from.name.compareTo(to.name) < 0) {
            first = from;
            second = to;
        } else {
            first = to;
            second = from;
        }

        try {
            System.out.println(Thread.currentThread().getName() +
                    " trying to lock FIRST " + first.name);
            first.lock.acquire();

            System.out.println(Thread.currentThread().getName() +
                    " locked FIRST " + first.name);

            Thread.sleep(100);

            System.out.println(Thread.currentThread().getName() +
                    " trying to lock SECOND " + second.name);
            second.lock.acquire();

            System.out.println(Thread.currentThread().getName() +
                    " locked SECOND " + second.name);

            // 💰 Critical section
            from.balance -= amount;
            to.balance += amount;

            System.out.println(Thread.currentThread().getName() +
                    " transfer completed");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 🔓 Always release in reverse order
            second.lock.release();
            first.lock.release();
        }
    }
}

public class WithoutDeadlockSimulation {
    public static void main(String[] args) {

        Account account1 = new Account("Account-1", 1000);
        Account account2 = new Account("Account-2", 1000);

        Thread t1 = new Thread(() ->
                Transfer.transfer(account1, account2, 100),
                "Thread-1"
        );

        Thread t2 = new Thread(() ->
                Transfer.transfer(account2, account1, 200),
                "Thread-2"
        );

        t1.start();
        t2.start();
    }
}