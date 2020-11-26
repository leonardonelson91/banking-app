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

import NumberFormat from 'react-number-format';

class BalancesList extends React.Component {
  constructor(props) {
      super(props);
      this.state = {
        error: null,
        isLoaded: false,
        acccount: {},
        accountId: props.match.params.id
      };
  }
  componentDidMount() {
    const { accountId } = this.state;
    fetch(`http://localhost:8082/reports/accounts/${accountId}`, {
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
            account: result
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
    const { error, isLoaded, account } = this.state;
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
            <Row>
              <Col md="12">
                <Card>
                  <CardHeader>
                    <CardTitle tag="h4">Account Balances</CardTitle>
                  </CardHeader>
                  <CardBody>
                    <Table className="tablesorter" responsive>
                      <thead className="text-primary">
                        <tr>
                          <th>Account ID</th>
                          <th>Currency</th>
                          <th>Amount</th>
                        </tr>
                      </thead>
                      <tbody>
                      {account.balances.map((balance, index) => (
                        <tr key={index}>
                          <td>{balance.accountId}</td>
                          <td>{balance.currency}</td>
                          <td><NumberFormat value={balance.amount} displayType="text" thousandSeparator={true} prefix={'$'} decimalScale={2}></NumberFormat></td>
                        </tr>
                      ))}
                      </tbody>
                    </Table>
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

export default BalancesList;
