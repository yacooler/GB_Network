package client.view;

public enum ViewPath {
    LOGIN_VIEW_PATH {
        public String getPath() {
            return "ClientApp/src/main/java/client/view/loginView.fxml";
        }
    },
    MAIN_VIEW_PATH {
        public String getPath() {
            return "ClientApp/src/main/java/client/view/mainView.fxml";
        }
    },
    ERROR_VIEW {
        public String getPath() {
            return "ClientApp/src/main/java/client/view/errorView.fxml";
        }
    },
    REGISTRATION_VIEW {
        public String getPath() {
            return "ClientApp/src/main/java/client/view/registrationView.fxml";
        }
    },
    CONNECTION_VIEW_PATH {
        public String getPath() {
            return "ClientApp/src/main/java/client/view/connectServerView.fxml";
        }
    };


    public abstract String getPath();
}
