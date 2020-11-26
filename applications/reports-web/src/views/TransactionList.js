import React from "react";

// reactstrap components
import {
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  Table,
  Row,
  Col,
  Alert
} from "reactstrap";

import { Link } from 'react-router-dom';

import NumberFormat from 'react-number-format';

import Moment from 'react-moment';

class TransactionList extends React.Component {
  constructor(props) {
      super(props);
      this.state = {
        error: null,
        isLoaded: false,
        transactions: [],
        accountId: props.match.params.id,
        newTransaction: props.history.location.state?.newTransaction
      };
  }
  componentDidMount() {
    const { accountId } = this.state;
    fetch(`http://localhost:8082/reports/accounts/${accountId}/transactions`, {
      mode: 'cors',
      headers: {
        'Access-Control-Allow-Origin':'*'
      }
    })
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            isLoaded: true,
            transactions: result
          });
        },
        (error) => {
          this.setState({
            isLoaded: true,
            error
          });
        }
      )
  }

  render() {
    const { error, isLoaded, transactions, accountId, newTransaction } = this.state;
    if (error) {
      return (
        <>
          <div className="content">
            <Alert color="danger">
              {error.message}
            </Alert>
          </div>
        </>
      );
    } else if (!isLoaded) {
      return (
        <>
          <div className="content">
            <Alert color="primary">
              Loading...
            </Alert>
          </div>
        </>
      );
    } else {
      return (
        <>
          <div className="content">
            {newTransaction ? 
              <Alert color="success">
                Transaction created. If you can't see it in the list, just wait for a couple of seconds so that the system is updated and then refresh the page to see the latest transaction.
              </Alert>
              :
              null
            } 
            <Row>
              <Col md="12">
                <Card>
                  <CardHeader>
                    <CardTitle tag="h4">Account Transactions</CardTitle>
                  </CardHeader>
                  <CardBody>
                    <Table className="tablesorter" responsive>
                      <thead className="text-primary">
                        <tr>
                          <th>Date</th>
                          <th>Transaction ID</th>
                          <th>Account ID</th>
                          <th>Amount</th>
                          <th>Currency</th>
                          <th>Direction</th>
                          <th>Description</th>
                          <th>Balance</th>
                        </tr>
                      </thead>
                      <tbody>
                      {transactions.map((transaction, index) => (
                        <tr key={index}>
                          <td><Moment format="DD/MM/YYYY HH:mm:ss" date={transaction.date}></Moment></td>
                          <td>{transaction.id}</td>
                          <td>{transaction.accountId}</td>
                          <td><NumberFormat value={transaction.amount} displayType="text" thousandSeparator={true} prefix={'$'} decimalScale={2}></NumberFormat></td>
                          <td>{transaction.currency}</td>
                          <td>{transaction.direction}</td>
                          <td>{transaction.description}</td>
                          <td><NumberFormat value={transaction.balance} displayType="text" thousandSeparator={true} prefix={'$'} decimalScale={2}></NumberFormat></td>
                        </tr>
                      ))}
                      </tbody>
                    </Table>
                    <br/>
                    <Link to={`/accounts/${accountId}/transaction`}>Create a new Transaction</Link> 
                  </CardBody>
                </Card>
              </Col>
            </Row>
          </div>
        </>
      );
    }
  }
}

export default TransactionList;
