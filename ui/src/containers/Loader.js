import React from 'react';
import PropTypes from "prop-types";

function Loader(props) {

  return  (
    <div className="loader" style={{display : props.isLoading ? 'auto' : 'none'}} />
  )
}

Loader.propTypes = {
  isLoading : PropTypes.bool
};

export default Loader;
