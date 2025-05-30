package com.branka;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceDirectory {

    public static Path get_RESOURCE_DIRECTORY(String one) {
        return Paths.get(
            "/Users/user", "code",
            "github",
            "game", "src", "main",
            "resources"
            , "branka",
            one
        );
    }

    public static Path get_RESOURCE_DIRECTORY() {
        return Paths.get(
            "/Users/user", "code",
            "github",
            "game", "src", "main",
            "resources"
            , "branka"
        );
    }


}
