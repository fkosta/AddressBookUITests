package qa.issart.com.tests;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;

public class HasTheOnlyElement<T> extends TypeSafeMatcher<Collection<T>> {

    private T elementIn;
    private String descriptionString;

    @Override
    public void describeTo(Description description) {
        description.appendText(descriptionString);
    }

    @Override
    protected boolean matchesSafely(Collection<T> elementsSet) {
        if(elementsSet.size()==1){
            T elementInSet = elementsSet.iterator().next();
            return elementInSet.equals(elementIn);
        }
        else {
            descriptionString = "Collection size is "+elementsSet.size()+"\n";
            descriptionString = descriptionString + elementsSet.toString();
            return false;
        }
    }

    public HasTheOnlyElement(T elementIn){
        this.elementIn = elementIn;
    }
}
