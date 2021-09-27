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
    private String descriptionString;

    @Override
    protected boolean matchesSafely(Set<T> elementsSet) {
        if((elementIn == null)&&(elementOut==null)){
            if(setOut!=null)
                elementsSetOut.removeAll(setOut);

            if(setIn!=null)
                elementsSetOut.addAll(setIn);

            descriptionString = "Elements in collection are: "+elementsSetOut.toString();
            return elementsSet.equals(elementsSetOut);
        }
        else{
            if(elementOut!=null)
                elementsSetOut.remove(elementOut);

            if(elementIn!=null)
                elementsSetOut.add(elementIn);

            descriptionString = "Elements in collection are: "+elementsSetOut.toString();
            return elementsSetOut.equals(elementsSet);
        }
    }

    public withElementsInOut(Set<T> elementsSetOut, T elementIn, T elementOut){
        this.elementIn = elementIn;
        this.elementOut = elementOut;
        this.elementsSetOut = elementsSetOut;
        this.setIn = null;
        this.setOut = null;
    }

    public withElementsInOut(Set<T> elementsSetOut, Collection<T> setIn, Collection<T> setOut){
        this.elementsSetOut = elementsSetOut;
        this.setIn = setIn;
        this.setOut = setOut;
        this.elementIn = null;
        this.elementOut = null;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(descriptionString);
    }
}
