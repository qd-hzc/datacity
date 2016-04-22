/**
 * 数组操作
 * Created by wxl on 2016/2/25.
 */
/**
 * 数组去重 使用hash方法
 * 缺点: 1和'1'会被认为重复项去掉
 * @param arr 要去重复的数组
 * @returns {Array} 去重后的数组
 */
function unique(arr) {
    var result = [], hash = {};
    for (var i = 0, elem; (elem = arr[i]) != null; i++) {
        if (!hash[elem]) {
            result.push(elem);
            hash[elem] = true;
        }
    }
    return result;
}

/**
 * 数组根据对象的value值去重 使用hash方法
 * 缺点: key为1和'1'会被认为重复项去掉
 * @param arr 要去重复的数组
 * @param key 要去重判断的值
 * @returns {Array} 去重后的数组
 */
function unique2(arr, key) {
    var result = [], hash = {};
    for (var i = 0, elem; (elem = arr[i]) != null; i++) {
        if (!hash[elem[key]]) {
            result.push(elem);
            hash[elem[key]] = true;
        }
    }
    return result;
}

