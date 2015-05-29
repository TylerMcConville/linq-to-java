import java.util.List;
import java.util.function.Function;

/**
 * Created by tmcconville on 5/29/2015.
 */
public class ListQueryFilterUtility{

    public static <T> T first(List<T> list, Function<T, Boolean> predicate) throws ElementNotFoundException{
        for(T element : list){
            if(predicate.apply(element)){
                return element;
            }
        }

        throw new ElementNotFoundException();
    }

    public static <T> T firstOrDefault(List<T> list, Function<T, Boolean> predicate){
        try{
            return first(list, predicate);
        }
        catch(ElementNotFoundException e){
            return null;
        }
    }



}
