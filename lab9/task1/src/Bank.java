public class Bank {

    /**
     * "atomic" еквівалент: блокуємо обидва рахунки і робимо
     * check-withdraw-deposit як одну критичну секцію.
     *
     * Deadlock уникаємо строгим порядком захоплення локів (за ID).
     */
    public void transfer(Account from, Account to, long amount) {
        if (from == null || to == null) throw new IllegalArgumentException("Accounts must not be null");
        if (from == to) return;
        if (amount <= 0) return;

        Account first = from.getId() < to.getId() ? from : to;
        Account second = from.getId() < to.getId() ? to : from;

        first.lock().lock();
        try {
            second.lock().lock();
            try {
                // критична секція: достатньо коштів -> withdraw+deposit
                if (from.withdrawUnsafe(amount)) {
                    to.depositUnsafe(amount);
                }
            } finally {
                second.lock().unlock();
            }
        } finally {
            first.lock().unlock();
        }
    }
}
