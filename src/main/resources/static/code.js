function addProduct() {
    var record = {
        type: document.getElementById("type").value,
        name: document.getElementById("name").value,
        price: document.getElementById("price").value,
        details: document.getElementById("details").value,
        stock: document.getElementById("stock").value,
    };

    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", insertProductIntoTable);
    xhr.open("POST", "/product/add");
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.send(JSON.stringify(record));
}

function removeProduct(id) {
    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", removeProductFromCart);
    xhr.open("GET", "/cart/remove?id=" + id);
    xhr.send();
}

function removeProductFromCart() {
    var id = this.responseText;
    var table = document.getElementById("cartTable");
    var row = document.getElementById("row_" + id);
    table.deleteRow(row.rowIndex);
}

function insertProductIntoTable() {
    var record = JSON.parse(this.responseText);

    var table = document.getElementById("productTable");
    var rows = table.querySelectorAll("tr");

    var new_row = table.insertRow(rows.length - 1);
    new_row.id = "row_" + record.id;
    var name_cell = new_row.insertCell(0);
    var price_cell = new_row.insertCell(1);
    var stock_cell = new_row.insertCell(2);
    name_cell.innerHTML = record.name;
    price_cell.innerHTML = record.price;
    stock_cell.innerHTML = record.stock;
    document.getElementById("name").value = "";
    document.getElementById("price").value = "";
    document.getElementById("stock").value = "";
    document.getElementById("details").value = "";
}

function filterByName() {
    var input, filter, table, tr, td, i, txtValue;
    input = document.getElementById("searchName");
    filter = input.value.toUpperCase();
    table = document.getElementById("productTable");
    tr = table.getElementsByTagName("tr");

    for (i = 0; i < tr.length; i++) {
        td = tr[i].getElementsByTagName("td")[1];
        if (td) {
            txtValue = td.textContent || td.innerText;
            if (txtValue.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}

function filterByType() {
    var input, filter, table, tr, td, i, txtValue;
    input = document.getElementById("searchType");
    filter = input.value.toUpperCase();
    table = document.getElementById("productTable");
    tr = table.getElementsByTagName("tr");

    for (i = 0; i < tr.length; i++) {
        td = tr[i].getElementsByTagName("td")[0];
        if (td) {
            txtValue = td.textContent || td.innerText;
            if (txtValue.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}

