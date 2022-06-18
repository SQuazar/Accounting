const Controller = {
    init: function () {
        this.getStocks();
    },

    createStock: function () {
        const stock_name = $('#stockName').val();
        const stock_address = $('#stockAddress').val();
        const stock = {
            'stock_name': stock_name,
            'stock_address': stock_address,
        };
        $.ajax({
            type: 'POST',
            url: '/api/stock/add',
            contentType: 'application/json',
            data: JSON.stringify(stock),
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                $('#stockName').val('');
                $('#stockAddress').val('');
                Controller.getStocks();
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    buildTableRows: function (data) {
        let rows = ``;
        for (let i = 0; i < data.length; i++) {
            rows += `<tr>
                        <td><a href="/products?stock_id=${data[i]['stock_id']}">${data[i]['stock_id']}</a></td>
                        <td>${data[i]['stock_name']}</td>
                        <td>${data[i]['stock_address']}</td>
                    </tr>`
        }
        return rows;
    },

    deleteStock: function () {
        const id = $('#deleteStockId');
        if (!Number.parseInt(id.val())) {
            console.log('???');
            id.val('');
            return;
        }
        $.ajax({
            type: 'POST',
            url: `/api/stock/delete?id=${id.val()}`,
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                id.val('');
                Controller.getStocks();
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },
    getStocks: function () {
        let sortType = $('#stockSortSelect').val();
        if (typeof sortType != 'string')
            sortType = 'none';
        const sortInverse = $('#inverseSort').is(':checked');
        $.ajax({
            type: 'POST',
            url: `/api/stock/all?sort=${sortType}&inverse=${sortInverse}`,
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function (data) {
                const tbody = $('#stockTableBody')[0];
                tbody.innerHTML = Controller.buildTableRows(data);
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    }
}