const Controller = {

    stocks: {},
    products: {},


    init: function () {
        Controller.getStocks(function () {
            Controller.getProducts();
        })
    },

    createProduct: function () {
        const productId = $('#productId').val();
        const name = $('#productName').val();
        const count = $('#productCount').val();
        const price = $('#productPrice').val();
        const type = $('#productRefundableSelect').val();
        const stockId = $('#stockSelect').val();
        const date = new Date($('#productDate').val());
        const purchase_date = parseToValidDate(date);
        const product = {
            'id': productId,
            'name': name,
            'count': count,
            'price': price,
            'refundable': type,
            'stock_id': stockId,
            'purchase_date': purchase_date
        };
        $.ajax({
            type: 'POST',
            url: '/api/product/save',
            contentType: 'application/json',
            data: JSON.stringify(product),
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                $('#productId').val('');
                $('#productName').val('');
                $('#productCount').val('');
                $('#productPrice').val('');
                $('#productRefundableSelect option').prop('selected', function () {
                    return this.defaultSelected;
                });
                $('#stockSelect option').prop('selected', function () {
                    return this.defaultSelected;
                });
                $('#productDate').val('');
                Controller.getProducts();
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
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
        const refundable = $('#productRefundableSearch').val();
        let stockId = getParamFromURL('stock_id');
        if (stockId === null)
            stockId = $('#stockProductSearch').val();
        let sortType = $('#productSortSelect').val();
        if (typeof sortType != 'string')
            sortType = 'none';
        const sortInverse = $('#inverseSort').is(':checked');
        $.ajax({
            type: 'POST',
            url: `/api/product/all?sort=${sortType}&inverse=${sortInverse}&${minDate === undefined ? '' : `min_date=${new Date(minDate).getTime()}&`}${maxDate === undefined ? '' : `max_date=${new Date(maxDate).getTime()}&`}${productName === '' ? '' : `name=${productName}&`}name_ignore_case=${nameIgnoreCase}&name_starts_with=${nameStartsWith}&${minCount === '' ? '' : `min_count=${minCount}&`}${maxCount === '' ? '' : `max_count=${maxCount}&`}${minPrice === '' ? '' : `min_price=${minPrice}&`}${maxPrice === '' ? '' : `max_price=${maxPrice}`}${refundable === 'none' ? '' : `refundable=${refundable}&`}${stockId === 'none' ? '' : `stock_id=${stockId}`}`,
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

    buildTableRows: function (data) {
        let rows = ``;
        for (let i = 0; i < data.length; i++) {
            Controller.products[data[i]['id'].toString()] = data[i];
            rows += `<tr>
                        <td><input class="form-check-input select-table-item" type="checkbox" value="${data[i]['id']}"></td>
                        <td>${data[i]['id']}</td>
                        <td>${data[i]['name']}</td>
                        <td>${data[i]['count']}</td>
                        <td>${data[i]['price']} руб./шт</td>
                        <td>${data[i]['refundable'] === true ? 'Да' : 'Нет'}</td>
                        <td>${data[i]['purchase_date']}</td>
                        <td>${Controller.stocks[data[i]['stock_id']]['stock_name']}</td>
                        ${Controller.getButtonsForTable(data[i]['id'])}
                    </tr>`
        }
        return rows;
    },

    getStocks: function (callback) {
        $.ajax({
            type: 'POST',
            url: '/api/stock/all',
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function (data) {
                const defaultOption = `<option selected value="none">Склад</option>`
                const stockSelect = $('#stockSelect')[0];
                const stockSearchSelect = $('#stockProductSearch')[0];
                const transferStockSelect = $('#transferStockSelect')[0];
                stockSelect.innerHTML = defaultOption;
                stockSearchSelect.innerHTML = defaultOption;
                for (let i = 0; i < data.length; i++) {
                    const stock = data[i];
                    Controller.stocks[stock['stock_id']] = stock;
                    const option = `<option value="${stock['stock_id']}">${stock['stock_name']}|${stock['stock_address']}</option>`
                    stockSelect.innerHTML += option;
                    stockSearchSelect.innerHTML += option;
                    transferStockSelect.innerHTML += option;
                }
                callback();
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    getStock: function (id) {
        return Controller.stocks[id];
    },

    deleteProduct: function (productId) {
        $.ajax({
            type: 'POST',
            url: `/api/product/delete?id=${productId}`,
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                Controller.getProducts();
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    selectAll: function () {
        $('.select-table-item').each(function (i, item) {
            item.checked = $('#selectBoxAll').is(":checked");
        })
    },

    transferProducts: function () {
        const productIds = [];
        let index = 0;
        $('.select-table-item').each(function (i, item) {
            if (item.checked) {
                productIds[index] = item.value;
                index++;
            }
        });
        $('#selectBoxAll').prop('checked', false);
        const stockId = $('#transferStockSelect').val();
        $.ajax({
            type: 'POST',
            url: `/api/product/transfer?${stockId === 'none' ? '' : `stock_id=${stockId}`}`,
            data: JSON.stringify(productIds),
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                Controller.getProducts();
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    deleteSelectedProducts: function () {
        const productIds = [];
        let index = 0;
        $('.select-table-item').each(function (i, item) {
            if (item.checked) {
                productIds[index] = item.value;
                index++;
            }
        });
        $('#selectBoxAll').prop('checked', false);
        $.ajax({
            type: 'POST',
            url: '/api/product/delete',
            data: JSON.stringify(productIds),
            contentType: 'application/json',
            beforeSend: function () {
                $('#loadingBackground').fadeIn(600);
            },
            success: function () {
                Controller.getProducts();
                $('#loadingBackground').fadeOut(600);
            },
            error: function (xhr) {
                const err = eval("(" + xhr.responseText + ")");
                showErrBlock(`${err.status}: ${err.message}`);
                $('#loadingBackground').fadeOut(600);
            }
        })
    },

    resetSearchFields: function () {
        $('#productSearchMinDate').val('');
        $('#productSearchMaxDate').val('');
        $('#productSearchName').val('');
        $('#productSearchNameIgnoreCase').prop('checked', false);
        $('#productNameSearchStartsWith').prop('checked', false)
        $('#productSearchMinCount').val('');
        $('#productSearchMaxCount').val('');
        $('#productSearchMinPrice').val('');
        $('#productSearchMaxPrice').val('');
        $('#productRefundableSearch option').prop('selected', function () {
            return this.defaultSelected;
        });
        $('#stockProductSearch option').prop('selected', function () {
            return this.defaultSelected;
        });
    },

    editProduct: function (productId) {
        const product = Controller.products[productId];
        $('#productId').val(product['id']);
        $('#productName').val(product['name']);
        $('#productCount').val(product['count']);
        $('#productPrice').val(product['price']);
        $('#productRefundableSelect option').prop('selected', function () {
            return product['refundable'] === this.value;
        });
        $('#stockSelect option').prop('selected', function () {
            return product['stock_id'].toString() === this.value;
        });
        $('#productDate').val(parseFromValidDate(product['purchase_date']));
    },

    getButtonsForTable: function (productId) {
        return `
            <td>
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                    class="bi bi-pencil edit-button" viewBox="0 0 16 16" onclick="window.scroll(0, 0);Controller.editProduct(${productId})">
                    <path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10zM11.207 2.5 13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293l6.5-6.5zm-9.761 5.175-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325z"/>
                </svg>
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="red"
                    cursor="pointer" class="bi bi-trash3" viewBox="0 0 16 16" onclick="Controller.deleteProduct(${productId})">
                    <path d="M6.5 1h3a.5.5 0 0 1 .5.5v1H6v-1a.5.5 0 0 1 .5-.5ZM11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3A1.5 1.5 0 0 0 5 1.5v1H2.506a.58.58 0 0 0-.01 0H1.5a.5.5 0 0 0 0 1h.538l.853 10.66A2 2 0 0 0 4.885 16h6.23a2 2 0 0 0 1.994-1.84l.853-10.66h.538a.5.5 0 0 0 0-1h-.995a.59.59 0 0 0-.01 0H11Zm1.958 1-.846 10.58a1 1 0 0 1-.997.92h-6.23a1 1 0 0 1-.997-.92L3.042 3.5h9.916Zm-7.487 1a.5.5 0 0 1 .528.47l.5 8.5a.5.5 0 0 1-.998.06L5 5.03a.5.5 0 0 1 .47-.53Zm5.058 0a.5.5 0 0 1 .47.53l-.5 8.5a.5.5 0 1 1-.998-.06l.5-8.5a.5.5 0 0 1 .528-.47ZM8 4.5a.5.5 0 0 1 .5.5v8.5a.5.5 0 0 1-1 0V5a.5.5 0 0 1 .5-.5Z"/>
                </svg>
            </td>
            `
    }

}