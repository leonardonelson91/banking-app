import AccountList from "views/AccountList";
import BalanceList from "views/BalancesList";
import TransactionList from "views/TransactionList";
import AccountCreate from "views/AccountCreate";
import CreateTransaction from "views/CreateTransaction";

var routes = [
  {
    path: "/accounts/all",
    component: AccountList,
  },
  {
    path: "/accounts/:id/balances",
    component: BalanceList,
  },
  {
    path: "/accounts/:id/transaction",
    component: CreateTransaction
  },
  {
    path: "/accounts/create",
    component: AccountCreate,
  },
  {
    path: "/accounts/:id/transactions",
    component: TransactionList
  }
];
export default routes;
