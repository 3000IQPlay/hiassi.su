package me.hollow.sputnik.api.property;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.client.events.ClientEvent;

import java.util.function.Predicate;

public class Setting<T> {

    private final String name;

    private final T defaultValue;
    private T value;

    private T min;
    private T max;
    private boolean hasRestriction;

    private Predicate<T> visibility;

    public Setting(String name, T defaultValue)  {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public Setting(String name, T defaultValue, T min, T max)  {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.hasRestriction = true;
    }

    public Setting(String name, T defaultValue, T min, T max, Predicate<T> visibility)  {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.visibility = visibility;
        this.hasRestriction = true;
    }

    public Setting(String name, T defaultValue, Predicate<T> visibility)  {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.visibility = visibility;
    }

    public final String getName() {
        return this.name;
    }

    public final T getValue() {
        return this.value;
    }

    public final T getMin() {
        return this.min;
    }

    public final T getMax() {
        return this.max;
    }

    public final void setValue(T value) {
        Main.INSTANCE.getBus().post(new ClientEvent(this));
        if (hasRestriction) {
            T plannedValue = value;

            if (((Number) min).floatValue() > ((Number) value).floatValue()) {
                plannedValue = min;
            }

            if (((Number) max).floatValue() < ((Number) value).floatValue()) {
                plannedValue = max;
            }

            this.value = plannedValue;

            return;
        }
        this.value = value;
    }

    public int getEnum(String input) {
        for (int i = 0; i < this.value.getClass().getEnumConstants().length; ++i) {
            final Enum e = (Enum)this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input)) {
                return i;
            }
        }
        return -1;
    }

    public void setEnumValue(String value) {
        for (Enum e : ((Enum) this.value).getClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) {
                this.value = (T)e;
            }
        }
    }

    public final String currentEnumName() {
        return EnumConverter.getProperName((Enum)this.value);
    }

    public void increaseEnum() {
        this.value = (T) EnumConverter.increaseEnum((Enum) this.value);
    }

    public String getType() {
        if(this.isEnumSetting()) {
            return "Enum";
        }
        return this.getClassName(this.defaultValue);
    }

    public <T> String getClassName(T value) {
        return value.getClass().getSimpleName();
    }

    public boolean isNumberSetting() {
        return (value instanceof Double || value instanceof Integer || value instanceof Short || value instanceof Long || value instanceof Float);
    }

    public boolean isEnumSetting() {
        return !isNumberSetting() && !(value instanceof Bind) && !(value instanceof String) && !(value instanceof Character) && !(value instanceof Boolean);
    }

    public boolean isStringSetting() {
        return value instanceof String;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public String getValueAsString() {
        return this.value.toString();
    }

    public boolean hasRestriction() {
        return this.hasRestriction;
    }

    public void setVisibility(Predicate<T> visibility) {
        this.visibility = visibility;
    }

    public boolean isVisible() {
        if (visibility == null) {
            return true;
        }
        return visibility.test(getValue());
    }
}
