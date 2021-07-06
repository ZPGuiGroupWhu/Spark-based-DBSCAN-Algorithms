// 内部函数不给外面调用
const INIT = Symbol('init');
const INIT_CONTEXT = Symbol('initContext');
const DRAW_GRADIENT = Symbol('drawGradient');
const GET_COLORS = Symbol('getColors');

class Gradient {
  _width = 0;
  _height = 1;
  _ctx = null;
  _colors = [];
  _gradients = [];

  constructor (colors, count = 101) {
    if (!Array.isArray(colors)) {
      throw new Error(
        `constructor param must be Array, current param is ${ colors }`,
      );
    }
    if (colors.length < 2) {
      throw new Error(
        `length of constructor param must >= 2, current length is ${
        colors.length
        }`,
      );
    }
    this._colors = colors;
    this._width = count;
    this[INIT]();
  }
  [INIT] () {
    this[INIT_CONTEXT]();
    this[DRAW_GRADIENT]();
    this[GET_COLORS]();
  }
  [INIT_CONTEXT] () {
    const canvas = document.createElement('canvas');
    canvas.setAttribute('width', this._width);
    canvas.setAttribute('height', this._height);
    this._ctx = canvas.getContext('2d');
  }
  [DRAW_GRADIENT] () {
    const { _ctx, _colors } = this;
    const gradient = _ctx.createLinearGradient(
      0,
      this._height / 2,
      this._width,
      this._height / 2,
    );
    const per = 1 / (_colors.length - 1);
    for (let i = 0; i < _colors.length; i++) {
      gradient.addColorStop(i * per, _colors[i]);
    }
    _ctx.fillStyle = gradient;
    _ctx.fillRect(0, 0, this._width, this._height);
  }
  [GET_COLORS] () {
    const { _ctx } = this;
    const imageData = _ctx.getImageData(0, this._height / 2, this._width, 1)
      .data;
    for (let i = 0; i < imageData.length; i += 4) {
      this._gradients.push([
        imageData[i],
        imageData[i + 1],
        imageData[i + 2],
        imageData[i + 3],
      ]);
    }
  }
  getColors () {
    return this._gradients;
  }
  getColor (index = 0) {
    if (index <= 0) {
      return this._colors[0];
    } else if (index >= this._width) {
      return this._colors[this._colors.length - 1];
    } else {
      index = Math.round(index - 1);
      const [r, g, b, a] = this._gradients[index];
      return [r, g, b, a];
    }
  }
}

export default Gradient;
