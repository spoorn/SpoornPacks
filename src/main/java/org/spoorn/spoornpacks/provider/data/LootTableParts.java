package org.spoorn.spoornpacks.provider.data;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class LootTableParts {

    @Getter
    @Builder
    static class Pool {
        int rolls;
        List<Entry> entries;
        List<Condition> conditions;
    }

    @Getter
    @Builder
    static class Entry {
        String type;
        String name;
        List<Child> children;
        List<Condition> conditions;
        List<Function> functions;
    }

    @Getter
    @Builder
    static class Child {
        String type;
        List<Condition> conditions;
    }

    @Getter
    @Builder
    static class Condition {
        String condition;
        String enchantment;
        List<Term> terms;
        List<Double> chances;
    }

    @Getter
    @Builder
    static class Term {
        String condition;
        Predicate predicate;
    }

    @Getter
    @Builder
    static class Predicate {
        List<String> items;
        List<Enchantment> enchantments;
    }

    @Getter
    @Builder
    static class Enchantment {
        String enchantment;
        Levels levels;
    }

    @Getter
    @Builder
    static class Levels {
        int min;
    }

    @Getter
    @Builder
    static class Function {
        String function;
        Count count;
    }

    @Getter
    @Builder
    static class Count {
        double min;
        double max;
        String type;
    }
}
