package employees.swing.model;

/**
 * Class used as model to save the pair of employee ids. The pair of employees who have worked
 * together on common projects
 * 
 * @param <K> - employee id
 * @param <V> - employee id
 */
public record PairIDs<K, V>(K key, V value) {
    public static <K, V> PairIDs<K, V> of(K key, V value) {
        return new PairIDs<>(key, value);
    }
}
