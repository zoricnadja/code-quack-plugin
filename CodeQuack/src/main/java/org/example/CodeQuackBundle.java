package org.example;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class CodeQuackBundle extends DynamicBundle {

    private static final String PATH_TO_BUNDLE = "messages.bundle";
    private static final CodeQuackBundle instance = new CodeQuackBundle();

    @Contract(pure = true)
    public static @Nls @NotNull String message(
            @NotNull @PropertyKey(resourceBundle = PATH_TO_BUNDLE) String key, Object... params) {
        return instance.getMessage(key, params);
    }

    private CodeQuackBundle() {
        super(PATH_TO_BUNDLE);
    }
}
