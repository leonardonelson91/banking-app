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

class AccountList extends React.Component {
  constructor(props) {
      super(props);
      this.state = {
        error: null,
        isLoaded: false,
        accounts: [],
        newAccount: props.history.location.state?.newAccount
      };
  }
  componentDidMount() {
    fetch("http://localhost:8082/reports/accounts", {
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
            accounts: result
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
    const { error, isLoaded, accounts, newAccount } = this.state;
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
          {newAccount ? 
              <Alert color="success">
                Account created. If you can't see it in the list, just wait for a couple of seconds so that the system is updated and then refresh the page to see the new account.
              </Alert>
              :
              null
            } 
            <Row>
              <Col md="12">
                <Card>
                  <CardHeader>
                    <CardTitle tag="h4">Accounts</CardTitle>
                  </CardHeader>
                  <CardBody>
                    <Table className="tablesorter" responsive>
                      <thead className="text-primary">
                        <tr>
                          <th>ID</th>
                          <th>Customer ID</th>
                          <th>Country</th>
                          <th>Balances</th>
                          <th>Transactions</th>
                        </tr>
                      </thead>
                      <tbody>
                      {accounts.map((account, index) => (
                        <tr key={index}>
                          <td>{account.id}</td>
                          <td>{account.customerId}</td>
                          <td>{account.country}</td>
                          <td><Link to={`/accounts/${account.id}/balances`}>See Balances</Link></td>
                          <td><Link to={`/accounts/${account.id}/transactions`}>See Transactions</Link></td>
                        </tr>
                      ))}
                      </tbody>
                    </Table>
                    <br/>
                    <Link to={`/accounts/create`}>Create a new Account</Link> 
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

export default AccountList;
