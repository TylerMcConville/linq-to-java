import java.util.ArrayList;
import java.util.function.Function;

/**
 * Created by tmcconville on 5/29/2015.
 */
public class LArrayList<T> extends ArrayList<T> {

    public T first(Function<T, Boolean> predicate) throws ElementNotFoundException {
        return ListQueryFilterUtility.first(this, predicate);
    }

    public T firstOrDefault(Function<T, Boolean> predicate){
        try{
            return first(predicate);
        }
        catch(ElementNotFoundException e){
            return null;
        }
    }

}
