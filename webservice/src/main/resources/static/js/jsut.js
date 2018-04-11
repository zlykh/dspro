class Wrapper extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            employees: []
        };
        this.loadEmployeesFromServer = this.loadEmployeesFromServer.bind(this);
    }

    render(){
        return (
            <div>
            <EmployeeTable employees={this.state.employees}/>
            <button className="btn btn-info" onClick={this.loadEmployeesFromServer}>reload</button>
            </div>
        );
    }
    componentDidMount(){
        this.loadEmployeesFromServer();
    }

    loadEmployeesFromServer() {
        var self = this;
        $.ajax({
            url: "http://localhost:8080/list"
        }).then(function (data) {
            console.log(data);
            self.setState({employees: data});
        });
    }
}
class Employee extends React.Component {
    render() {
        return (
            <tr>
                <td>{this.props.employee.ctn}</td>
                <td>{this.props.employee.name}</td>
                <td>{this.props.employee.age}</td>
            </tr>
        );
    }
}
class EmployeeTable extends React.Component {
    render() {
        var rows = [];
        this.props.employees.forEach(function(employee) {
            rows.push(<Employee employee={employee} key={employee.ctn} />);
        });
        return (
            <div>
                employee table
                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th>Ctn</th><th>Name</th><th>Age</th>
                    </tr>
                    </thead>
                    <tbody>{rows}</tbody>
                </table>
            </div>
        );
    }
}


var EMPLOYEES = [
    {name: 'Joe Biden', age: 45, ctn: 123},
    {name: 'President Obama', age: 54, ctn: 456},
    {name: 'Crystal Mac', age: 34, ctn: 789},
    {name: 'James Henry', age: 33, ctn: 111}
];

ReactDOM.render(
    // <EmployeeTable employees={EMPLOYEES} />, document.getElementById('root')
    <Wrapper />, document.getElementById('root')
);