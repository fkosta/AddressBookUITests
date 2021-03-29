package qa.issart.com.tests;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import qa.issart.com.models.*;

import java.util.Collection;
import java.util.Set;

public class withElementsInOut<T> extends TypeSafeMatcher<Set<T>> {
    private T elementOut;
    private T elementIn;
    private Set<T> elementsSetOut;
    private Collection<T> setIn;
    private Collection<T> setOut;
    private String operation;
    private String descriptionString;

    @Override
    protected boolean matchesSafely(Set<T> elementsSet) {
        if((elementIn == null)&&(elementOut==null)){
            if(!operation.equals("creation"))
                elementsSetOut.removeAll(setOut);

            if(!operation.equals("deletion"))
                elementsSetOut.addAll(setIn);

            descriptionString = "Elements in collection are: "+elementsSet.toString();
            return elementsSet.equals(elementsSetOut);
        }
        else if(elementOut==null) {
            if (elementsSet.size() == 1) {
                T elementInSet = elementsSet.iterator().next();
                descriptionString = "Element in collection is "+elementInSet.toString();
                return elementInSet.equals(elementIn);
            } else {
                descriptionString = "Collection size is "+elementsSet.size()+"\n";
                descriptionString = descriptionString + elementsSet.toString();
                return false;
            }
        }
        else{
            elementsSetOut.remove(elementOut);
            if(elementIn!=null)
                elementsSetOut.add(elementIn);

            descriptionString = "Elements in collection are: "+elementsSet.toString();
            return elementsSetOut.equals(elementsSet);
        }
    }

    public withElementsInOut(T elementIn){
        this.elementIn = elementIn;
        this.elementOut = null;
    }

    public withElementsInOut(Set<T> elementsSetOut, T elementIn, T elementOut){
        this.elementIn = elementIn;
        this.elementOut = elementOut;
        this.elementsSetOut = elementsSetOut;
        this.operation = "modification";
    }

    public withElementsInOut(Set<T> elementsSetOut, Collection<T> setIn, Collection<T> setOut){
        this.elementsSetOut = elementsSetOut;
        this.setIn = setIn;
        this.setOut = setOut;
        this.operation = "modification";
        this.elementIn = null;
        this.elementOut = null;
    }

    public withElementsInOut(Set<T> elementsSetOut, Set<T> diffSet, String operation){
        this.elementsSetOut = elementsSetOut;
        if(operation.equals("creation"))
            this.setIn = diffSet;
        else
            this.setOut = diffSet;

        this.operation = operation;
        this.elementIn = null;
        this.elementOut = null;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(descriptionString);
    }
}
