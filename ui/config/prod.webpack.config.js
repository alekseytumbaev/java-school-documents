const path = require('path');
const WebpackPwaManifest = require('webpack-pwa-manifest');
const CompressionPlugin = require('compression-webpack-plugin');

module.exports = {
    mode: 'production',

    entry: path.join(process.cwd(), 'src/index.js'),
    output: {
        filename: 'main.js',
        path: path.join(process.cwd(), 'dist'),
        clean: true,
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
        new CompressionPlugin({
            algorithm: 'gzip',
            test: /\.js$|\.css$|\.html$/,
            threshold: 10240,
            minRatio: 0.8,
        }),

        new WebpackPwaManifest({
            name: 'React Boilerplate',
            short_name: 'React BP',
            description: 'My React Boilerplate-based project!',
            background_color: '#fafafa',
            theme_color: '#b1624d',
            inject: true,
            ios: true
        }),
    ]
};
