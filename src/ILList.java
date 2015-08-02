import java.util.List;
import java.util.function.Function;

/**
 * Created by tmcconville on 6/3/2015.
 */
public interface ILList<T, L extends List<T>>{

    //TODO thenby, thenbydescending
    // and whatever else sounds cool

    //TODO many of these methods have a return type (usually a list)
    // but also have side effects on the original list
    // Should we clone the original list before performing any operations on it?

    // These generics are getting ridiculous
    // I think this is only working because of type erasure (instantiating a new LS that's the same class as L with a different generic type)
    default <LS extends List<S> & ILList<S, List<S>>, S> LS selectMany(Function<T, List<S>> selector){
        L list = (L)this;
        LS selected = null;
        try {
            selected = (LS) this.getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        for (T element : list){
            List<S> individual = selector.apply(element);
            selected.addRange(individual);
        }

        return selected;
    }

    default L addRange(List<T> second){
        L list = (L)this;

        second.forEach(list::add);
        return list;
    }

    // TODO there's probably a more efficient way to do this
    default L union(List<T> second){
        L unioned = null;
        try {
            unioned = (L) this.getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        for (T element : addRange(second)){
            if (unioned.contains(element) == false){
                unioned.add(element);
            }
        }

        return unioned;
    }

    // I can't find a clean way to implement this
    // In C#, you can do something like...
    // myList.OfType<String>()
    // However, it doesn't look like Java supports that syntax
    // You could pass the type you want as a parameter (see current implementation)
    // But there's no way to enforce that "Sa" is of type "S"
    // So I think it's better to leave this off for now
//    default <LS extends List<S>, S> LS ofType(Class Sa){
//        L list = (L)this;
//        LS values = null;
//        try{
//            values = (LS)this.getClass().newInstance();
//        }
//        catch(InstantiationException e){
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        for(T element : list){
//            if (element.getClass() == Sa){
//                values.add((S)element);
//            }
//        }
//
//        return values;
//    }

    //TODO is there a better way to do this?
    // Would need to find another way of instantiating the implementing class,
    // but with a generic type of S rather than T
    // Feels -very- bad to create a new instance of L and cast it to LS (but maybe it isn't bad? I'm honestly not sure)
    default <LS extends List<S> & ILList<S, List<S>>, S> LS select(Function<T, S> predicate){
        L list = (L)this;
        LS values = null;
        try {
            values = (LS)this.getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (T element : list){
            S value = predicate.apply(element);
            values.add(value);
        }

        return values;
    }

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

    default boolean none(Function<T, Boolean> predicate){
        return any(predicate) == false;
    }

    // TODO shouldn't be publicly accessible, but has to be due to implementing these as default methods. Alternatives?
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

    // TODO shouldn't be publicly accessible, but has to be due to implementing these as default methods. Alternatives?
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
