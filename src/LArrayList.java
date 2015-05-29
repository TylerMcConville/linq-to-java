import java.util.ArrayList;
import java.util.List;
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

    public LArrayList<T> where(Function<T, Boolean> predicate){
        List<T> filtered = ListQueryFilterUtility.where(this, predicate);
        LArrayList<T> filteredLArrayList = new LArrayList<>();
        for (T element : filtered){
            filteredLArrayList.add(element);
        }

        return filteredLArrayList;
    }

}
