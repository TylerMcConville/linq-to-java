import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by tmcconville on 5/29/2015.
 */
public class LArrayList<T> extends ArrayList<T> {
    //TODO look into default methods for interfaces. Could define an interface that
    // defines every one of these methods by default except for copyList.
    // That way, to "extend" a List implementation, you'd just need to implement the new interface
    // and provide an implementation of copyList

    public T first(Function<T, Boolean> predicate) throws ElementNotFoundException {
        return ListQueryFilterUtility.first(this, predicate);
    }

    public T firstOrDefault(Function<T, Boolean> predicate){
        return ListQueryFilterUtility.firstOrDefault(this, predicate);
    }

    public LArrayList<T> where(Function<T, Boolean> predicate){
        List<T> filtered = ListQueryFilterUtility.where(this, predicate);
        return copyList(filtered);
    }

    public <R extends Comparable> LArrayList<T> orderBy(Function<T, R> predicate){
        return copyList(ListQueryFilterUtility.orderBy(this, predicate));
    }

    public <R extends Comparable> LArrayList<T> orderByDescending(Function<T, R> predicate){
        return copyList(ListQueryFilterUtility.orderByDescending(this, predicate));
    }

    public <R extends Comparable> R min(Function<T, R> predicate){
        return ListQueryFilterUtility.min(this, predicate);
    }

    public <R extends Comparable> R max(Function<T, R> predicate){
        return ListQueryFilterUtility.max(this, predicate);
    }

    public boolean any(Function<T, Boolean> predicate){
        return ListQueryFilterUtility.any(this, predicate);
    }

    private static <T> LArrayList<T> copyList(List<T> source){
        LArrayList<T> destination = new LArrayList<>();
        for(T element : source){
            destination.add(element);
        }

        return destination;
    }

}
