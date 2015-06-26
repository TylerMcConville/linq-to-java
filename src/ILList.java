import java.util.List;
import java.util.function.Function;

/**
 * Created by tmcconville on 6/3/2015.
 */
public interface ILList<T, L extends List<T>>{

    //TODO none, select, thenby, thenbydescending, selectmany, union
    // and whatever else sounds cool

    default T first(Function<T, Boolean> predicate) throws ElementNotFoundException{
        L list = (L)this;
        for(T element : list){
            if(predicate.apply(element)){
                return element;
            }
        }

        throw new ElementNotFoundException();
    }

    default T firstOrDefault(Function<T, Boolean> predicate){
        try{
            return first(predicate);
        }
        catch(ElementNotFoundException e){
            return null;
        }
    }

    default L where(Function<T, Boolean> predicate){
        L list = (L)this;
        L filtered = null;
        try {
            filtered = (L)this.getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (T element : list){
            if (predicate.apply(element)){
                filtered.add(element);
            }
        }

        return filtered;
    }

    default<R extends Comparable> L orderBy(Function<T, R> predicate){
        return orderByInternal(predicate, true);
    }

    default<R extends Comparable> L orderByDescending(Function<T, R> predicate){
        return orderByInternal(predicate, false);
    }

    default <R extends Comparable> R max(Function<T, R> predicate){
        return minOrMaxInternal(predicate, false);
    }

    default <R extends Comparable> R min(Function<T, R> predicate){
        return minOrMaxInternal(predicate, true);
    }

    default boolean any(Function<T, Boolean> predicate){
        L list = (L)this;
        for (T element : list){
            if (predicate.apply(element)){
                return true;
            }
        }

        return false;
    }

    default boolean all(Function<T, Boolean> predicate){
        L list = (L)this;
        for (T element : list){
            if (predicate.apply(element) == false){
                return false;
            }
        }

        return true;
    }

    default <R extends Comparable> R minOrMaxInternal(Function<T, R> predicate, boolean min){
        L list = (L)this;
        R currentBest = null;
        for(T element : list){
            if (element == null){
                continue;
            }

            if (currentBest == null){
                currentBest = predicate.apply(element);
                continue;
            }
            //TODO try/catch
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
    default <R extends Comparable> L orderByInternal(Function<T, R> predicate, boolean ascending){
        L list = (L)this;
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
                    R currentPredicateResult =  predicate.apply(currentElement);

                    R nextPredicateResult = predicate.apply(nextElement);

                    // Purposely not try/catching a NullPointer here
                    // This could error in the case of comparing two complex comparable objects if one of those objects is null
                    // But I think it should be the job of the person that implements the Comparable interface to ensure that a NPE is not thrown in that case
                    result = currentPredicateResult.compareTo(nextPredicateResult);
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
