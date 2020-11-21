package client.controller;

import client.view.ViewFactory;
import lombok.Getter;
import lombok.Setter;

/**
 * BaseController - для управления контроллерами
 */
public abstract class BaseController {
    protected ViewFactory viewFactory;
    @Getter
    @Setter
    private String fxmlPath;

    public BaseController(ViewFactory viewFactory, String fxmlPath) {
        this.viewFactory = viewFactory;
        this.fxmlPath = fxmlPath;
    }
}
