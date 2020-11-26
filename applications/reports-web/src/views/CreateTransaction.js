import React from "react";

// reactstrap components
import {
  Button,
  Card,
  CardHeader,
  CardBody,
  CardFooter,
  FormGroup,
  Form,
  Input,
  Row,
  Col,
  Alert
} from "reactstrap";

class CreateTransaction extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      accountId: props.match.params.id,
      account: null
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
          const currencies = result.balances.map(balance => balance.currency);

          this.setState({
            isLoaded: true,
            account: result,
            currencies
          });
        },
        (error) => {
          this.setState({
            isLoaded: true,
            error
          });
        }
      )

    this.postTransaction = (event) => {
      event.preventDefault();
      const data = new FormData(event.target);
  
      const transaction = {
        'amount': data.get('amount'),
        'currency': data.get('currency'),
        'direction': data.get('direction'),
        'description': data.get('description'),
      };

      console.log(transaction);
  
      fetch(`http://localhost:8080/accounts/${accountId}/transaction`, {
        mode: 'cors',
        method: 'post',
        headers: {
          'Access-Control-Allow-Origin':'*',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(transaction)
      })
      .then(
        res => this.props.history.push(`/accounts/${accountId}/transactions`, { newTransaction: true }),
        (error) => {
          this.setState({
            isLoaded: true,
            error
          });
      });
    }
  }

  render() {
    const { error, isLoaded, account, currencies } = this.state;
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
          <Form onSubmit={this.postTransaction}>
            <Row>
              <Col md="8">
                <Card>
                  <CardHeader>
                    <h5 className="title">Create Transaction</h5>
                  </CardHeader>
                  <CardBody>
                      <Row>
                        <Col className="pr-md-1" md="5">
                          <FormGroup>
                            <label>Customer</label>
                            <Input
                              defaultValue={account.customerId}
                              disabled
                              type="text"
                            />
                          </FormGroup>
                        </Col>
                        <Col className="px-md-1" md="5">
                          <FormGroup>
                            <label>Account</label>
                            <Input
                              defaultValue={account.id}
                              disabled
                              type="text"
                            />
                          </FormGroup>
                        </Col>
                      </Row>
                      <Row>
                        <Col className="pr-md-1" md="4">
                          <FormGroup>
                            <label>Amount</label>
                            <Input
                              name="amount"
                              defaultValue="0.0"
                              type="number"
                              step="any"
                              min="0.01"
                            />
                          </FormGroup>
                        </Col>
                        <Col className="pl-md-1" md="4">
                          <FormGroup>
                            <label>Direction</label>
                            <select name="direction" className="form-control">
                              <option>IN</option>
                              <option>OUT</option>
                            </select>
                          </FormGroup>
                        </Col>
                        <Col className="pl-md-1" md="4">
                          <FormGroup>
                            <label>Currency</label>
                            <select name="currency" className="form-control">
                            {currencies.map((currency, index) => (
                              <option key={index}>{currency}</option>
                            ))}
                            </select>
                          </FormGroup>
                        </Col>
                      </Row>
                      <Row>
                        <Col md="8">
                          <FormGroup>
                            <label>Description</label>
                            <Input
                              required
                              name="description"
                              cols="80"
                              placeholder="Enter the transaction's description"
                              rows="4"
                              type="textarea"
                            />
                          </FormGroup>
                        </Col>
                      </Row>
                  </CardBody>
                  <CardFooter>
                    <Button className="btn-fill" color="primary" type="submit">
                      Save
                    </Button>
                  </CardFooter>
                </Card>
              </Col>
            </Row>
          </Form>
          </div>
        </>
      );
    }
  }
}

export default CreateTransaction;
