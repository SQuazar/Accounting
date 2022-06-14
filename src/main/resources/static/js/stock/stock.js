const Controller = {

    init: function () {
        this.getProducts();
    },

    getProducts: function () {
        let minDate = $('#productSearchMinDate').val();
        if (minDate === '')
            minDate = undefined;
        let maxDate = $('#productSearchMaxDate').val();
        if (maxDate === '')
            maxDate = undefined;
        const productName = $('#productSearchName').val();
        const nameIgnoreCase = $('#productSearchNameIgnoreCase').is(':checked');
        const nameStartsWith = $('#productNameSearchStartsWith').is(':checked')
        const minCount = $('#productSearchMinCount').val();
        const maxCount = $('#productSearchMaxCount').val();
        const minPrice = $('#productSearchMinPrice').val();
        const maxPrice = $('#productSearchMaxPrice').val();
        let sortType = $('#productSortSelect').val();
        if (typeof sortType != 'string')
            sortType = 'none';
        const sortInverse = $('#inverseSort').is(':checked');
        $.ajax({
            type: 'POST',
            url: `/api/stock/all?sort=${sortType}&inverse=${sortInverse}&${minDate === undefined ? '' : `min_date=${new Date(minDate).getTime()}&`}${maxDate === undefined ? '' : `max_date=${new Date(maxDate).getTime()}&`}${productName === '' ? '' : `name=${productName}&`}name_ignore_case=${nameIgnoreCase}&name_starts_with=${nameStartsWith}&${minCount === '' ? '' : `min_count=${minCount}&`}${maxCount === '' ? '' : `max_count=${maxCount}&`}${minPrice === '' ? '' : `min_price=${minPrice}&`}${maxPrice === '' ? '' : `max_price=${maxPrice}`}`,
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function (data) {
                const tbody = $('#productsTableBody')[0];
                tbody.innerHTML = Controller.buildTableRows(data);
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    createProduct: function () {
        const product_name = $('#productName').val();
        const product_count = $('#productCount').val();
        const product_price = $('#productPrice').val();
        const date = new Date($('#productDate').val());
        const product_date = parseToValidDate(date);
        const product = {
            'product_name': product_name,
            'count': product_count,
            'price': product_price,
            'delivery_date': product_date
        };
        $.ajax({
            type: 'POST',
            url: '/api/stock/add',
            contentType: 'application/json',
            data: JSON.stringify(product),
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                $('#productName').val('');
                $('#productCount').val('');
                $('#productPrice').val('');
                $('#productDate').val('');
                Controller.getProducts();
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
                        <td>${data[i]['product_id']}</td>
                        <td>${data[i]['product_name']}</td>
                        <td>${data[i]['count']}</td>
                        <td>${data[i]['price']} руб.</td>
                        <td>${data[i]['delivery_date']}</td>
                    </tr>`
        }
        return rows;
    },

    deleteProduct: function () {
        const id = $('#deleteProductId');
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
                Controller.getProducts();
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    }

}