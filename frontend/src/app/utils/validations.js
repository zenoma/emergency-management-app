export const emailValidation = (value) => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/i.test(value);

export const phoneNumberValidation = (value) => value.match(/\d/g).length === 9;

export const dniValidation = (value) => {
  if (/^\d{8}[a-zA-Z]$/.test(value)) {
    var dniLetter = value.substring(8, 9).toUpperCase();
    var dniNumb = parseInt(value.substring(0, 8));
    var letters = [
      "T",
      "R",
      "W",
      "A",
      "G",
      "M",
      "Y",
      "F",
      "P",
      "D",
      "X",
      "B",
      "N",
      "J",
      "Z",
      "S",
      "Q",
      "V",
      "H",
      "L",
      "C",
      "K",
      "E",
      "T",
    ];
    var rightLetter = letters[dniNumb % 23];
    return dniLetter === rightLetter;
  }
};
