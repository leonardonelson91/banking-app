import React from "react";
import ReactDOM from "react-dom";
import { createBrowserHistory } from "history";
import { Router, Route, Switch, Redirect } from "react-router-dom";

import Home from "views/Home.js";

import "assets/scss/black-dashboard-react.scss";
import "assets/css/nucleo-icons.css";

const hist = createBrowserHistory();

ReactDOM.render(
  <Router history={hist}>
    <Switch>
      <Route path="/accounts" render={props => <Home {...props} />} />
      <Redirect from="/" to="/accounts/all" />
    </Switch>
  </Router>,
  document.getElementById("root")
);
