function filterLookup() {
    var input, filter, table, tr, td, i, txtValue;
    input = document.getElementById("searchInput");
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

function addProduct() {
    var record = {
        type : document.getElementById("type").value,
        name : document.getElementById("name").value,
        name : document.getElementById("details").value,
        name : document.getElementById("price").value,
    };

    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", insertPersonIntoTable);
    xhr.open("POST", "/product/add");
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.send(JSON.stringify(record));
}


function removeProduct(id) {
    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", removeProductFromCart);
    xhr.open("GET", "/cart/remove?id="+id);
    xhr.send();
}

function removeProductFromCart() {
    var id = this.responseText;
    var table = document.getElementById("cartTable");
    var row = document.getElementById("row_" + id);
    table.deleteRow(row.rowIndex);
}

/* function insertPersonIntoTable() {
    var record = JSON.parse(this.responseText);

    var table = document.getElementById("productTable");
    var rows = table.querySelectorAll("tr");

    var new_row = table.insertRow(rows.length-1);
    new_row.id = "row_"+record.id;
    var name_cell = new_row.insertCell(0);
    var price_cell = new_row.insertCell(1);
    var detail_cell = new_row.insertCell(2);
    var addCart_cell = new_row.insertCell(3);
    name_cell.innerHTML = record.name;
    price_cell.innerHTML = record.price;
    detail_cell.innerHTML = "<button>Detail</button>"
    addCart_cell.innerHTML = "<button>Add to Cart</button>"
    document.getElementById("name").value="";
    document.getElementById("price").value="";
} */
