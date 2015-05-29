import java.util.ArrayList;
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

    public static <T> List<T> where(List<T> list, Function<T, Boolean> predicate){
        List<T> filtered = new ArrayList<>();
        for (T element : list){
            if (predicate.apply(element)){
                filtered.add(element);
            }
        }

        return filtered;
    }

    public static<T, R extends Comparable> List<T> orderBy(List<T> list, Function<T, R> predicate){
        return orderByInternal(list, predicate, true);
    }

    public static<T, R extends Comparable> List<T> orderByDescending(List<T> list, Function<T, R> predicate){
        return orderByInternal(list, predicate, false);
    }

    public static <T, R extends Comparable> R max(List<T> list, Function<T, R> predicate){
        return minOrMaxInternal(list, predicate, false);
    }

    public static <T, R extends Comparable> R min(List<T> list, Function<T, R> predicate){
        return minOrMaxInternal(list, predicate, true);
    }

    private static<T, R extends Comparable> R minOrMaxInternal(List<T> list, Function<T, R> predicate, boolean min){
        R currentBest = null;
        for(T element : list){
            if (element == null){
                continue;
            }

            if (currentBest == null){
                currentBest = predicate.apply(element);
            }

            R currentPredicateResult = predicate.apply(element);
            int result = currentBest.compareTo(currentPredicateResult);

            if ((!min && result < 0) || (min && result > 0)){
                currentBest = currentPredicateResult;
            }
        }

        return currentBest;
    }

    // Not up-to-date on my sort algorithms
    // Doing it quick, dirty, and inefficient for now
    private static <T, R extends Comparable> List<T> orderByInternal(List<T> list, Function<T, R> predicate, boolean ascending){
        boolean needToSort = true;
        while(needToSort){
            needToSort = false;
            for(int i = 0; i < list.size() - 1; i++){
                T currentElement = list.get(i);
                T nextElement = list.get(i + 1);

                if (currentElement == null && nextElement == null){
                    continue;
                }

                boolean switchElements;
                int result;

                if (currentElement == null){
                    result = -1;
                } else if (nextElement == null){
                    result = 1;
                }else{
                    result = predicate.apply(currentElement).compareTo(predicate.apply(nextElement));
                }

                if (ascending){
                    switchElements = result > 0;
                }else{
                    switchElements = result < 0;
                }

                if (switchElements){
                    needToSort = true;
                    list.set(i + 1, currentElement);
                    list.set(i, nextElement);
                }
            }
        }

        return list;
    }


}
