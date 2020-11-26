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

class AccountCreate extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      error: null,
      currencies: []
    };
  }

  componentDidMount() {
    fetch(`http://localhost:8080/accounts/currencies`, {
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
            currencies: result
          });
        },
        (error) => {
          this.setState({
            isLoaded: true,
            error
          });
        }
      )
      
    this.postAccount = (event) => {
      event.preventDefault();
      const data = new FormData(event.target);
  
      const account = {
        'customerId': data.get('customerId'),
        'currencies': data.getAll('currencies'),
        'country': data.get('country')
      };
  
      fetch(`http://localhost:8080/accounts`, {
        mode: 'cors',
        method: 'post',
        headers: {
          'Access-Control-Allow-Origin':'*',
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(account)
      })
      .then(
        (res) => {
          if (res.ok) {
            this.props.history.push(`/accounts`)
          } else {
            res.text().then(text => {
                this.setState({
                  isLoaded: true,
                  error: {
                    message: text
                  }
                });
            });
          }
        },
        (error) => {
          this.setState({
            isLoaded: true,
            error
          });
      });
    }
  }

  render() {
    const { error, isLoaded, currencies } = this.state;
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
          <Form onSubmit={this.postAccount}>
            <Row>
              <Col md="8">
                <Card>
                  <CardHeader>
                    <h5 className="title">Create Account</h5>
                  </CardHeader>
                  <CardBody>
                      <Row>
                        <Col className="pr-md-1" md="5">
                          <FormGroup>
                            <label>Customer</label>
                            <Input
                              required
                              name="customerId"
                              placeholder="Enter the Customer ID"
                              type="text"
                            />
                          </FormGroup>
                        </Col>
                        <Col className="px-md-1" md="5">
                          <FormGroup>
                            <label>Country</label>
                            <Input
                              required
                              name="country"
                              placeholder="Enter the Country"
                              type="text"
                            />
                          </FormGroup>
                        </Col>
                      </Row>
                      <Row>
                        <Col className="pr-md-1" md="4">
                          <FormGroup>
                            <label>Currencies</label>
                            <select required multiple={true} name="currencies" className="form-control">
                            {currencies.map((currency, index) => (
                              <option key={index}>{currency.code}</option>
                            ))}
                            </select>
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

export default AccountCreate;
