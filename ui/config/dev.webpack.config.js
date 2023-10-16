const path = require('path');
const HtmlWebPackPlugin = require('html-webpack-plugin');

module.exports = {
    mode: 'development',

    entry: './src/index.js',
    output: {
        filename: 'main.js',
        path: path.join(process.cwd(), 'dist'),

    },
    devServer: {
        contentBase: [
            path.join(process.cwd(), 'public'),
            path.join(process.cwd(), 'src'),
        ],
        compress: true,
        port: 9000,
        watchContentBase: true,
        progress: true
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                resolve: {
                    extensions: [".js", ".jsx"]
                },
                use: {
                    loader: "babel-loader"
                }
            },
            {
                test: /\.css$/i,
                use: ['style-loader', 'css-loader'],
            },
            {
                test: /\.(png|svg|jpg|gif)$/,
                use: ["file-loader"]
            },
        ],
    },
    plugins: [
        new HtmlWebPackPlugin({
            template:path.join(process.cwd(), 'public/index.html'),
            filename: 'index.html'
        })
    ]
};