package dev.aj.full_stack_v5.practice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedCollection;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@Slf4j
@Disabled
class PracticeTest {

    @Test
    void test() throws NoSuchFieldException, ClassNotFoundException {
        CustomSet customSet = new CustomSet();
        customSet.columnOrder.add("a");
        customSet.columnOrder.add("b");
        customSet.columnOrder.add("c");
        customSet.columnOrder.add("d");

        Class<?> aClass = customSet.columnOrder.getClass();
        Field colOrderField = customSet.getClass().getDeclaredField("columnOrder");
        Type columnOrder = colOrderField.getGenericType();
        Class<? extends Type> columnOrderClass = columnOrder.getClass();
        Class<?> genericColumnClass = Class.forName(columnOrder.getTypeName().substring(columnOrder.getTypeName().indexOf("<") + 1, columnOrder.getTypeName().indexOf(">")));
        log.info("Column Order Class: {}", columnOrderClass);

        Collection<String> originalList = Collections.unmodifiableCollection(customSet.columnOrder);

        switch (columnOrder.getTypeName()) {
            case "java.util.List":
                List<String> list =  new ArrayList<>(customSet.columnOrder.size())  ;
                list.stream().forEachOrdered(item -> list.add(item.concat(" - Updated")));
                log.info("List: {}", list);
                break;
            case "java.util.ArrayList":
                ArrayList<String> arrayList = new ArrayList<>(customSet.columnOrder.size())  ;
                customSet.columnOrder.stream().forEachOrdered(item -> arrayList.add(item.concat(" - Updated")));
                log.info("ArrayList: {}", arrayList);
                break;
            case "java.util.Set":
                Set<String> set = new LinkedHashSet<>(customSet.columnOrder.size());

        }

        if (SequencedCollection.class.isAssignableFrom(aClass)) {
            SequencedCollection sequencedCollection = (SequencedCollection) aClass.cast(customSet.columnOrder);

            if (List.class.isAssignableFrom(aClass)) {
                List list = (List) aClass.cast(customSet.columnOrder);
            }

            sequencedCollection.stream().forEachOrdered(item -> ((String) item).concat(" - Updated"));
            log.info("List: {}", sequencedCollection);
        }
    }

    private class CustomSet{
        List<String> columnOrder = new ArrayList<>();
    }

}
